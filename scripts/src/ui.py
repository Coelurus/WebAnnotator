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
        self.root = ttk.Window(themename="darkly")
        self.root.title("Gesture Recording")
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)
        self.is_recording = False
        self.last_position = None

        main_container = ttk.Frame(self.root, padding=20)
        main_container.pack(fill=BOTH, expand=YES)

        left_panel = ttk.LabelFrame(main_container, text="Controls", padding=15)
        left_panel.pack(side=LEFT, fill=Y, padx=(0, 10))

        instructions_frame = ttk.LabelFrame(left_panel, text="Instructions", padding=10)
        instructions_frame.pack(fill=X, pady=(0, 15))

        steps = [
            "Choose a camera and image size",
            "Click on 'Start recording'",
            "Carry out the gestures",
            "Click on 'Stop recording'",
        ]

        for idx, step in enumerate(steps, start=1):
            label = ttk.Label(instructions_frame, text=f"{idx}. {step}", justify="left")
            label.pack(anchor='w', pady=2)

        controls_frame = ttk.LabelFrame(left_panel, text="Recording Settings", padding=10)
        controls_frame.pack(fill=X, pady=(0, 15))

        self.record_button = ttk.Button(
            controls_frame, 
            text="Start recording", 
            bootstyle="success-outline-toolbutton",
            width=20,
            command=self.toggle_recording
        )
        self.record_button.pack(pady=10)

        data_frame = ttk.LabelFrame(left_panel, text="Camera Settings", padding=10)
        data_frame.pack(fill=X)

        right_panel = ttk.Frame(main_container)
        right_panel.pack(side=RIGHT, fill=BOTH, expand=YES)

        if not SensorUtils.initialize():
            ttk.messagebox.showerror("Error", "Could not open sensor device.")
            exit(SENSOR_FAILURE_EXIT_CODE)

        self.prepare_recording_settings(data_frame)
        self.prepare_cam(right_panel)
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
            self.record_button.config(
                text="Start recording",
                bootstyle="success-outline-toolbutton"
            )
            self.is_recording = False
            self.finish_recording()
        else:
            self.record_button.config(
                text="Stop recording",
                bootstyle="danger-outline-toolbutton"
            )
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
        input()
        clean_folder(REL_TEMP_SAVE_PATH)

    def camera_changed(self, *args):
        """Handle camera change event."""
        self.video_capture = cv2.VideoCapture(self.chosen_camera_idx.get())

    def prepare_recording_settings(self, parent):
        """Prepare the camera settings. Find available cameras and set up the UI for camera selection and image preview."""
        size_frame = ttk.Frame(parent)
        size_frame.pack(fill=X, pady=5)
        
        self.image_size = tk.IntVar(value=100)
        self.size_label = ttk.Label(size_frame, text="Image Size:")
        self.size_label.pack(anchor='w')
        
        size_slider = ttk.Scale(
            size_frame,
            from_=MIN_IMAGE_SIZE,
            to=MAX_IMAGE_SIZE,
            variable=self.image_size,
            orient=HORIZONTAL,
            bootstyle="info",
            command=lambda val: self.image_size.set(round(float(val) / IMAGE_SIZE_STEP) * IMAGE_SIZE_STEP)
        )
        size_slider.pack(fill=X, pady=(5, 0))
        
        self.size_value_label = ttk.Label(size_frame, text=f"{self.image_size.get()} px")
        self.size_value_label.pack(anchor='e')
        
        period_frame = ttk.Frame(parent)
        period_frame.pack(fill=X, pady=5)
        
        self.period_ms = tk.IntVar(value=3)
        self.period_label = ttk.Label(period_frame, text="Recording Period:")
        self.period_label.pack(anchor='w')
        
        period_slider = ttk.Scale(
            period_frame,
            from_=MIN_PERIOD_MS,
            to=MAX_PERIOD_MS,
            variable=self.period_ms,
            orient=HORIZONTAL,
            bootstyle="info",
            command=self.period_ms.set
        )
        period_slider.pack(fill=X, pady=(5, 0))
        
        self.period_value_label = ttk.Label(period_frame, text=f"{self.period_ms.get()} ms")
        self.period_value_label.pack(anchor='e')

        self.image_size.trace_add("write", self.update_size_label)
        self.period_ms.trace_add("write", self.update_period_label)

    def prepare_cam(self, parent):
        """Prepare the camera settings."""
        data_frame = ttk.LabelFrame(parent, text="Data Preview", padding=10)
        data_frame.pack(fill=BOTH, expand=YES)

        sensor_frame = ttk.LabelFrame(data_frame, text="Sensor Data", padding=10)
        sensor_frame.pack(fill=X, pady=(0, 15))

        x_frame = ttk.LabelFrame(sensor_frame, text="X", padding=10)
        x_frame.pack(fill=X, pady=(0, 15))
        self.x_label = ttk.Label(x_frame, text="0")
        self.x_label.pack(anchor='w')

        y_frame = ttk.LabelFrame(sensor_frame, text="Y", padding=10)
        y_frame.pack(fill=X, pady=(0, 15))
        self.y_label = ttk.Label(y_frame, text="0")
        self.y_label.pack(anchor='w')

        z_frame = ttk.LabelFrame(sensor_frame, text="Z", padding=10)
        z_frame.pack(fill=X, pady=(0, 15))
        self.z_label = ttk.Label(z_frame, text="0")
        self.z_label.pack(anchor='w')

        camera_frame = ttk.LabelFrame(data_frame, text="Camera", padding=10)
        camera_frame.pack(fill=X, pady=(0, 15))

        foundIndices = self.findAvailableCameraIndexes()
        self.chosen_camera_idx = tk.IntVar(value=foundIndices[0])

        if len(foundIndices) == 0:
            ttk.messagebox.showerror("Error", "No cameras found.")
            exit(NO_CAMERA_EXIT_CODE)
        elif len(foundIndices) != 1:
            camera_combobox = ttk.Combobox(
                camera_frame,
                textvariable=self.chosen_camera_idx,
                state="readonly",
                bootstyle="info"
            )
            camera_combobox['values'] = foundIndices
            camera_combobox.current(0)
            camera_combobox.pack(pady=(0, 10))
            self.chosen_camera_idx.trace_add("write", self.camera_changed)
        else:
            self.camera_changed()

        self.current_image = None
        self.canvas = tk.Canvas(
            camera_frame,
            width=self.image_size.get(),
            height=self.image_size.get(),
            bg='black'
        )
        self.canvas.pack(fill=BOTH, expand=YES, pady=10)

        self.progress_label = ttk.Label(
            data_frame,
            text="IDLE",
            bootstyle="info"
        )
        self.progress_label.pack(pady=5)

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

        self.update_sensor_labels()
        self.root.after(15, self.update_cam)   

    def update_sensor_labels(self):
        """Update the sensor labels with the current position."""
        x, y, z = SensorUtils.get_position()
        self.x_label.config(text=f"{x}")
        self.y_label.config(text=f"{y}")
        self.z_label.config(text=f"{z}")


    def update_size_label(self, *args):
        """Update the size label."""
        self.size_value_label.config(text=f"{self.image_size.get()} px")

    def update_period_label(self, *args):
        """Update the period label."""
        self.period_value_label.config(text=f"{self.period_ms.get()} ms")

if __name__ == "__main__":
    app = App()