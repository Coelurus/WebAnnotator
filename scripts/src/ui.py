import tkinter as tk
import cv2
from PIL import Image, ImageTk
import ttkbootstrap as ttk
from ttkbootstrap.constants import *

from utils.video_utils import on_click, record_video, save_frames
from utils.file_utils import get_timestamps, zip_files, clean_temp
from utils.constants import CAMERA_INDEX_RANGE, MIN_IMAGE_SIZE, MAX_IMAGE_SIZE, IMAGE_SIZE_STEP

class App:
    def __init__(self):
        self.root = tk.Tk()

        b1 = ttk.Button(self.root, text="Button 1", bootstyle=SUCCESS)
        b1.pack(side=LEFT, padx=5, pady=10)

        b2 = ttk.Button(self.root, text="Button 2", bootstyle=(INFO, OUTLINE))
        b2.pack(side=LEFT, padx=5, pady=10)

        self.prepare_recording_settings()

        self.prepare_cam()


        self.root.mainloop()

    def camera_changed(self, *args):
        self.video_capture = cv2.VideoCapture(self.chosen_camera_idx.get())

    def prepare_cam(self):
        self.chosen_camera_idx = tk.IntVar() 
        camera_combobox = ttk.Combobox(self.root, textvariable = self.chosen_camera_idx)
        camera_combobox['values'] = self.findAvailableCameraIndexes()
        camera_combobox.current(0)
        camera_combobox.pack()
        self.chosen_camera_idx.trace_add("write", self.camera_changed)
        self.camera_changed()

        self.current_image = None
        self.canvas = tk.Canvas(self.root, width=self.image_size.get(), height=self.image_size.get())
        self.canvas.pack()

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
            self.current_image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)).resize((self.image_size.get(), self.image_size.get()))

            self.photo = ImageTk.PhotoImage(image=self.current_image)

            self.canvas.create_image(0,0,image=self.photo, anchor=tk.NW)
        
        self.root.after(15, self.update_cam)   
    
    def get_image_sizes(self):
        image_sizes: list[int] = []
        for i in range(MIN_IMAGE_SIZE, MAX_IMAGE_SIZE+1, IMAGE_SIZE_STEP):
            image_sizes.append(i)
        return image_sizes

    def prepare_recording_settings(self):
        self.image_size = tk.IntVar() 
        camera_combobox = ttk.Combobox(self.root, textvariable = self.image_size)
        camera_combobox['values'] = self.get_image_sizes()
        camera_combobox.current(len(self.get_image_sizes()) -1)
        camera_combobox.pack()

if __name__ == "__main__":
    app = App()