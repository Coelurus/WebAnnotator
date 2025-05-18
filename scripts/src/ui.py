import tkinter as tk
import cv2
from PIL import Image, ImageTk
import ttkbootstrap as ttk
from ttkbootstrap.constants import *
import csv

from utils.sensor_utils import SensorUtils
from utils.file_utils import zip_files, clean_folder, ensure_directory_exists
from utils.constants import *
import datetime
import uuid

class App:
    def __init__(self):
        self.root = tk.Tk()
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)
        self.is_recording = False
        self.last_position = None

        instructions_frame = ttk.Frame(self.root)
        instructions_frame.pack(side=LEFT, padx=10, pady=10, anchor='n')

        if not SensorUtils.initialize():
            tk.messagebox.showerror("Error", "Could not open sensor device.")
            exit(SENSOR_FAILURE_EXIT_CODE)

        steps = [
            "Choose a camera and image size",
            "Click on 'Start recording'",
            "Carry out the gestures",
            "Click on 'Stop recording'",
        ]

        for idx, step in enumerate(steps, start=1):
            label = ttk.Label(instructions_frame, text=f"{idx}. {step}", justify="left")
            label.pack(anchor='w', pady=2)

        self.record_button = ttk.Button(instructions_frame, text="Start recording", bootstyle=SUCCESS, command=self.toggle_recording)
        self.record_button.pack(anchor='w', pady=(10, 0))

        self.prepare_recording_settings()
        self.prepare_cam()
        self.root.mainloop()

    def on_closing(self):
        """Handle the window close event."""
        if self.is_recording:
            self.finish_recording()
        SensorUtils.shutdown()
        self.root.destroy()

    def toggle_recording(self):
        """Toggle the recording state."""
        if self.is_recording:
            self.record_button.config(text="Start recording", bootstyle=SUCCESS)
            self.is_recording = False
            self.finish_recording()
        else:
            self.record_button.config(text="Stop recording", bootstyle=DANGER)
            self.is_recording = True
            self.start_recording()

    def start_recording(self):
        """Prepare the recording process."""
        ensure_directory_exists(REL_TEMP_SAVE_PATH)

        self.recording_name = "recording_" + datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
        self.csv_file = open(f"{REL_TEMP_SAVE_PATH}{self.recording_name}.csv", mode='w', newline='', encoding='utf-8')
        self.csv_writer = csv.writer(self.csv_file)
        self.csv_writer.writerow(['timestamp', 'image-name', 'x', 'y', 'z']) 

        self.progress_label.config(text="Recording...")
        self.record()

    def record(self):
        """Capture images and save them with timestamps."""
        if self.is_recording:
            now = datetime.datetime.now()
            timestamp = now.strftime("%Y%m%d_%H%M%S_%f")
            unique_id = uuid.uuid4().hex
            file_name = f"{timestamp}_{unique_id}.webp"
            x, y, z = SensorUtils.get_position()

            if self.last_position is not None and (x, y, z) != self.last_position:
                self.current_image.save(f"{REL_TEMP_SAVE_PATH}{file_name}")
                self.csv_writer.writerow([now.strftime("%Y-%m-%d %H:%M:%S"), file_name, x, y, z])

            self.last_position = (x, y, z)
            self.root.after(self.period_ms.get(), self.record) 

    def finish_recording(self):
        """Finish the recording process. Zip the files and clean up."""
        self.csv_file.close()
        self.progress_label.config(text="Finished...")

        zip_files(self.recording_name, OUT_PATH, REL_TEMP_SAVE_PATH)
        clean_folder(REL_TEMP_SAVE_PATH)

    def camera_changed(self, *args):
        """Handle camera change event."""
        self.video_capture = cv2.VideoCapture(self.chosen_camera_idx.get())

    def prepare_cam(self):
        """Prepare the camera settings. Find available cameras and set up the UI for camera selection and image preview."""
        foundIndices = self.findAvailableCameraIndexes()
        self.chosen_camera_idx = tk.IntVar(value = foundIndices[0])

        if len(foundIndices) == 0:
            tk.messagebox.showerror("Error", "No cameras found.")
            exit(NO_CAMERA_EXIT_CODE)
        elif len(foundIndices) != 1:
            camera_label = ttk.Label(self.root, text="Choose a camera:")
            camera_label.pack()

            camera_combobox = ttk.Combobox(self.root, textvariable=self.chosen_camera_idx, state="readonly")         
            camera_combobox['values'] = foundIndices
            camera_combobox.current(0)
            camera_combobox.pack()

            self.chosen_camera_idx.trace_add("write", self.camera_changed)
        else:
            self.camera_changed()

        self.current_image = None
        self.canvas = tk.Canvas(self.root, width=self.image_size.get(), height=self.image_size.get())
        self.canvas.pack()

        self.progress_label = ttk.Label(self.root, text="IDLE")
        self.progress_label.pack()

        self.update_cam()

    def findAvailableCameraIndexes(self) -> list[int]:
        """Looks through possible indexes and returns list with indices of found cameras"""
        found_cameras: list[int] = []
        for i in range(CAMERA_INDEX_RANGE):
            cap = cv2.VideoCapture(i)
            if cap.read()[0]:
                found_cameras.append(i)
                cap.release()
        return found_cameras

    def update_cam(self):
        """Update the camera feed in the canvas."""
        ret, frame = self.video_capture.read()

        if ret:
            self.current_image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)).resize(
                (self.image_size.get(), self.image_size.get())
            )

            self.photo = ImageTk.PhotoImage(image=self.current_image)

            canvas_width = self.canvas.winfo_width()
            canvas_height = self.canvas.winfo_height()
            image_width = self.image_size.get()
            image_height = self.image_size.get()

            x_offset = (canvas_width - image_width) // 2
            y_offset = (canvas_height - image_height) // 2

            self.canvas.delete("all")
            self.canvas.create_image(x_offset, y_offset, image=self.photo, anchor=tk.NW)

        
        self.root.after(15, self.update_cam)   


    def prepare_recording_settings(self):
        """Prepare the recording settings UI. Create sliders for image size and period."""
        self.image_size = tk.IntVar(value=100)

        self.size_label = ttk.Label(self.root, text=f"Choose video size: {self.image_size.get()} px")
        self.size_label.pack()

        size_slider = ttk.Scale(self.root, from_=MIN_IMAGE_SIZE, to=MAX_IMAGE_SIZE, variable=self.image_size, orient=HORIZONTAL, command=lambda val: self.image_size.set(round(float(val) / IMAGE_SIZE_STEP) * IMAGE_SIZE_STEP))
        size_slider.pack()

        self.image_size.trace_add("write", self.update_size_label)

        self.period_ms = tk.IntVar(value=3)

        self.period_label = ttk.Label(self.root, text=f"Choose period: {self.period_ms.get()} ms")
        self.period_label.pack()

        period_slider = ttk.Scale(self.root, from_=MIN_PERIOD_MS, to=MAX_PERIOD_MS, variable=self.period_ms, orient=HORIZONTAL, command=self.period_ms.set)
        period_slider.pack()

        self.period_ms.trace_add("write", self.update_period_label)

    def update_size_label(self, *args):
        """Update the label to show the current value of the slider."""
        self.size_label.config(text=f"Choose video size: {self.image_size.get()} px")

    def update_period_label(self, *args):
        """Update the label to show the current value of the slider."""
        self.period_label.config(text=f"Choose period: {self.period_ms.get()} ms")

if __name__ == "__main__":
    app = App()