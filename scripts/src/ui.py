import threading
import tkinter as tk
import cv2
from PIL import Image, ImageTk
from pynput import mouse
import ttkbootstrap as ttk
from ttkbootstrap.constants import *

from utils.log_utils import LogFileParser
from utils.video_utils import on_click, record_video, save_frames
from utils.file_utils import get_log_file_name, zip_files, clean_temp
from utils.constants import CAMERA_INDEX_RANGE, MIN_IMAGE_SIZE, MAX_IMAGE_SIZE, IMAGE_SIZE_STEP, REL_TEMP_SAVE_PATH, OUT_PATH

class App:
    def __init__(self):
        self.root = tk.Tk()

        instructions_frame = ttk.Frame(self.root)
        instructions_frame.pack(side=LEFT, padx=10, pady=10, anchor='n')

        steps = [
            "Choose a camera and image size",
            "Connect sensor",
            "Open Aurea app",
            "Click on 'Start recording' bellow",
            "Click on 'REC' in Aurea",
            "Wait till the setting finishes",
            "Carry out the gestures",
            "Click on 'Stop recording' in Aurea",
        ]

        for idx, step in enumerate(steps, start=1):
            label = ttk.Label(instructions_frame, text=f"{idx}. {step}", justify="left")
            label.pack(anchor='w', pady=2)

        record_button = ttk.Button(instructions_frame, text="Start recording", bootstyle=SUCCESS, command=self.start_recording)
        record_button.pack(anchor='w', pady=(10, 0))

        self.prepare_recording_settings()

        self.prepare_cam()

        self.root.mainloop()

    def start_recording(self):
        self.progress_label.config(text="Preparing...")

        listener = mouse.Listener(on_click=on_click)
        listener.start()

        self.video_capture.release()

        self.progress_label.config(text="Click the REC button...")
        self.root.update_idletasks()

        recording_thread = threading.Thread(
            target=self.run_recording, 
            args=(listener,), 
            daemon=True
        )
        recording_thread.start()

    def camera_changed(self, *args):
        self.video_capture = cv2.VideoCapture(self.chosen_camera_idx.get())

    def run_recording(self, listener):
        self.progress_label.config(text="Recording...")
        record_video(self.chosen_camera_idx.get())
        self.root.after(0, lambda: self.finish_recording(listener))

    def finish_recording(self, listener):
        listener.stop()
        listener.join()
        self.camera_changed()
        self.progress_label.config(text="Finished...")

        log_file_name = get_log_file_name(REL_TEMP_SAVE_PATH)

        print("Video and log file created")
        print("Parsing log file into csv")
        log_file_parser = LogFileParser(REL_TEMP_SAVE_PATH + log_file_name, REL_TEMP_SAVE_PATH + "output.csv")
        log_file_parser.parse()

        print("Loading timestamps from log file")
        timestamps = log_file_parser.get_timestamps()

        print("Extracting matching frames")
        save_frames(timestamps)

        print("Creating ZIP file")
        zip_files(log_file_name, OUT_PATH, REL_TEMP_SAVE_PATH)

        print("Cleaning debris")
        clean_temp()
        print("Getting data was successful")

    def prepare_cam(self):
        self.chosen_camera_idx = tk.IntVar()

        camera_label = ttk.Label(self.root, text="Choose a camera:")
        camera_label.pack()

        camera_combobox = ttk.Combobox(self.root, textvariable=self.chosen_camera_idx, state="readonly")
        foundIndices = self.findAvailableCameraIndexes()

        if len(foundIndices) == 0:
            print("No cameras found")
            exit(1)
        else:
            print(f"Found {len(foundIndices)} camera{'s' if len(foundIndices) != 1 else ''}")
        
        camera_combobox['values'] = foundIndices
        camera_combobox.current(0)
        camera_combobox.pack()

        self.chosen_camera_idx.trace_add("write", self.camera_changed)
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
    
    def get_image_sizes(self):
        image_sizes: list[int] = []
        for i in range(MIN_IMAGE_SIZE, MAX_IMAGE_SIZE+1, IMAGE_SIZE_STEP):
            image_sizes.append(i)
        return image_sizes

    def prepare_recording_settings(self):
        self.image_size = tk.IntVar(value=100)

        self.size_label = ttk.Label(self.root, text=f"Choose video size: {self.image_size.get()} px")
        self.size_label.pack()

        size_slider = ttk.Scale(self.root, from_=MIN_IMAGE_SIZE, to=MAX_IMAGE_SIZE, variable=self.image_size, orient=HORIZONTAL, command=lambda val: self.image_size.set(round(float(val) / IMAGE_SIZE_STEP) * IMAGE_SIZE_STEP))
        size_slider.pack()

        self.image_size.trace_add("write", self.update_size_label)

    def update_size_label(self):
        self.size_label.config(text=f"Choose video size: {self.image_size.get()} px")

if __name__ == "__main__":
    app = App()