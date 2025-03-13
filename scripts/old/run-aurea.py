import os
import sys
import time
import shutil
import psutil
import subprocess
import win32gui
import win32con
import win32process
import pickle
from pynput import mouse

from PyQt5.QtWidgets import QFileDialog, QMainWindow, QApplication
import tkinter as tk
from tkinter import filedialog, ttk

import cv2
from PIL import Image, ImageTk

class AureaConnector:
    SCRIPT_DATA_FILE = "data.pickle"

    root: tk.Tk = None
    aurea_path: str = None
    aurea_name: str = None
    camera_id: int = 0
    tracking_active = False
    aurea_window_handle:int = None

    coords_label: tk.Label = None

    def __init__(self):
        current_directory = os.path.dirname(os.path.abspath(__file__))
        if os.path.isfile(os.path.join(current_directory, self.SCRIPT_DATA_FILE)):
            with open(self.SCRIPT_DATA_FILE, 'rb') as data_file:
                data: dict[str, str] = pickle.load(data_file)
                self.aurea_path = data["aurea_path"] if "aurea_path" in data else None 
                self.aurea_name = data["aurea_name"]  if "aurea_name" in data else None
                self.camera_id = data["camera_id"] if "camera_id" in data else None

    def save_data(self):
        with open(self.SCRIPT_DATA_FILE, 'wb') as data_file:
            data = dict()
            data["aurea_path"] = self.aurea_path
            data["aurea_name"] = self.aurea_name
            data["camera_id"] = self.camera_id
            pickle.dump(data, data_file, protocol=pickle.HIGHEST_PROTOCOL)

    def select_folder_with_aurea(self) -> tuple[str, str]:
        """Let user select Aurea.exe executable and return path to its directory"""
        
        if self.aurea_path and self.aurea_name:
            pass
        else:
            aurea_path = filedialog.askopenfilename(title="Select Aurea.exe")
            aurea_name = aurea_path.split("/")[-1]
            aurea_path = "/".join(aurea_path.split("/")[0:-1])
            self.aurea_path, self.aurea_name = aurea_path, aurea_name
            
        return self.aurea_path, self.aurea_name

    def copy_file_to_folder(self, file_name: str, destination_folder: str) -> None:
        """Copy script to run Aurea.exe to the same directory where the .exe file is"""
        current_directory = os.path.dirname(os.path.realpath(__file__))
        source_file = os.path.join(current_directory, file_name)
        
        if os.path.isfile(source_file):
            shutil.copy(source_file, destination_folder)
            print(f"File {file_name} copied to {destination_folder}")
        else:
            print(f"File {file_name} does not exist in the current directory.")

    def start_aurea(self, aurea_directory: str, script_name:str) -> None:
        """Run the copied script and start Aurea app"""
        script_path = os.path.join(aurea_directory, script_name)
        subprocess.Popen(script_path, shell=False)

    def get_aurea_pid(self, aurea_name: str) -> int:
        """Get PID of running aurea process. Returns ID of process if it exists, -1 otherwise"""
        for proc in psutil.process_iter():
            if aurea_name in proc.name():
                return proc.pid
        return -1

    def find_aurea_window(self, aurea_pid: int):
        """Find the Aurea window"""

        def callback(hwnd, results):
            _, found_pid = win32process.GetWindowThreadProcessId(hwnd)
            if found_pid == aurea_pid and win32gui.IsWindowVisible(hwnd):
                results.append(hwnd)
            return True
        
        windows = []
        win32gui.EnumWindows(callback, windows)
        return windows[0] if windows else None

    def kill_aurea(self, aurea_pid):
        """Kill Aurea proccess nested in tkinter window"""
        try:
            process = psutil.Process(aurea_pid)
            process.terminate()  
            process.wait(timeout=5)
        except psutil.NoSuchProcess:
            pass
        except psutil.TimeoutExpired:
            process.kill()

    def embed_aurea(self, aurea_pid):
        """Embed aurea window into custom tkinter window"""

        def start_camera():
            """Start the camera and display the feed in the camera frame"""
            cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)

            def update_frame():
                """Update frame displayed every 10 ms"""
                ret, frame = cap.read()
                if ret:
                    frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                    img = Image.fromarray(frame)
                    img = img.resize((500, 400))  
                    imgtk = ImageTk.PhotoImage(image=img)
                    camera_label.imgtk = imgtk
                    camera_label.configure(image=imgtk)

                camera_label.after(10, update_frame)

            camera_label = ttk.Label(camera_frame)
            camera_label.pack(fill=tk.BOTH, expand=False)

            def on_close():
                """Release camera when window is closed"""
                cap.release()
                camera_frame.destroy()

            def stop_camera():
                """Stop the camera feed"""
                cap.release()
                camera_label.config(image='')
                start_button.config(state=tk.NORMAL)

            stop_button = tk.Button(button_frame, text="Stop Camera", command=stop_camera)
            stop_button.grid(row=0, column=1, padx=10, pady=10)

            start_button.config(state=tk.DISABLED) 

            update_frame()

        def on_closing():
            """Save data and clean up before exiting """
            self.save_data()
            self.kill_aurea(aurea_pid)
            root.destroy()

        root = tk.Tk()
        self.root = root
        root.title("Embedded Aurea App")
        root.resizable(False, False)
        window_width, window_height = 1600, 800
        root.geometry(f"{window_width}x{window_height}")

        aurea_frame_width, aurea_frame_height = 1100, 800
        embed_frame = tk.Frame(root, width=aurea_frame_width - 10, height=aurea_frame_height - 60)
        embed_frame.grid(row=0, column=0, rowspan=3, sticky="nsew")
        embed_frame.grid_propagate(False)

        #cover_frame = tk.Label(embed_frame, width=aurea_frame_width, height=aurea_frame_height)
        #cover_frame.place(x=0, y=0, width=aurea_frame_width, height=aurea_frame_height)

        camera_frame = tk.Frame(root, width=500, height=400, bg="black")
        camera_frame.grid(row=0, column=1, sticky="nsew")
        camera_frame.grid_propagate(False)

        camera_label = tk.Label(camera_frame)
        camera_label.pack(fill=tk.BOTH, expand=True)

        button_frame = tk.Frame(root, width=500, height=400)
        button_frame.grid(row=1, column=1, sticky="nsew")
        button_frame.grid_propagate(False)

        start_button = tk.Button(button_frame, text="Start Camera", command=start_camera)
        start_button.grid(row=0, column=1, padx=10, pady=10)

        save_button = tk.Button(button_frame, text="Save Rec Cords", command=self.activate_tracking)
        save_button.grid(row=1, column=1, padx=10, pady=10)

        self.coords_label = tk.Label(root, text="Right-click to save coordinates")
        self.coords_label.grid(row=2, column=1, padx=10, pady=10)

        mock_button_2 = tk.Button(button_frame, text="Mock Button 2")
        mock_button_2.grid(row=1, column=2, padx=10, pady=10)

        mock_button_3 = tk.Button(button_frame, text="Mock Button 3")
        mock_button_3.grid(row=1, column=3, padx=10, pady=10)

        root.update_idletasks()
        embed_frame.update_idletasks()
        embed_hwnd = embed_frame.winfo_id()

        self.aurea_window_handle = self.find_aurea_window(aurea_pid)
        

        while not win32gui.IsWindow(self.aurea_window_handle) or not win32gui.IsWindowVisible(self.aurea_window_handle) or win32gui.GetWindowText(self.find_aurea_window(aurea_pid)) == "Aurea":
            time.sleep(0.1)
            self.aurea_window_handle = self.find_aurea_window(aurea_pid)

        win32gui.SetParent(self.aurea_window_handle, embed_hwnd)
        win32gui.SetWindowPos(
            self.aurea_window_handle,
            None,
            0, -30, 
            aurea_frame_width, aurea_frame_height, 
            win32con.SWP_NOZORDER | win32con.SWP_NOACTIVATE | win32con.SWP_FRAMECHANGED
        )

        style = win32gui.GetWindowLong(self.aurea_window_handle, win32con.GWL_STYLE)
        #style &= ~win32con.WS_CAPTION
        style &= ~win32con.WS_THICKFRAME
        #style &= ~win32con.WS_DISABLED
        #style &= ~win32con.WS_POPUPWINDOW
        style &= ~win32con.WS_MINIMIZEBOX
        style &= ~win32con.WS_MAXIMIZEBOX
        win32gui.SetWindowLong(self.aurea_window_handle, win32con.GWL_STYLE, style)

        """
        style = win32gui.GetWindowLong(self.aurea_window_handle, win32con.GWL_STYLE)
        new_style = style & ~win32con.WS_CAPTION & ~win32con.WS_THICKFRAME
        win32gui.SetWindowLong(self.aurea_window_handle, win32con.GWL_STYLE, new_style)
        """
        #win32gui.SetForegroundWindow(aurea_window)

        #win32gui.EnableWindow(aurea_window, True)

        # start_camera(camera_frame)
        root.protocol("WM_DELETE_WINDOW", on_closing)
        root.mainloop()

    def wait_for_aurea_window(self, aurea_pid: int):
        """Wait until Aurea starts and displays its final window"""
        while(win32gui.GetWindowText(self.find_aurea_window(aurea_pid)) == "Aurea"):
            print("Wade in the waaater")
            time.sleep(0.1)
        time.sleep(1)
        print("is this skipped?")
        return win32gui.GetWindowText(self.find_aurea_window(aurea_pid))

    def get_coords(self, event: tk.Event):
        """Get coordinates of the Aurea Rec button based on mouse click"""
        if self.tracking_active:
            x, y = event.x, event.y
            self.coords_label.config(text=f"Coordinates: ({x}, {y})")
            self.disable_tracking()

    def activate_tracking(self):
        """Activate mouse tracking to locate Record button in Aurea"""
        self.tracking_active = True
        self.coords_label.config(text="Tracking activated! Right-click the Rec button.")
        self.root.bind("<Button-3>", self.get_coords)

    def disable_tracking(self):
        """Disable mouse tracking after click"""
        self.tracking_active = False
        self.root.unbind("<Button-3>")

    def get_embedded_window_rect(self):
        """Get the rectangle of the embedded window"""
        if self.aurea_window_handle:
            return win32gui.GetWindowRect(self.aurea_window_handle)
        return None

    def start_mouse_listener(self):
        """Start a global mouse listener to track clicks"""
        def on_click(x, y, button, pressed):
            if pressed and button == mouse.Button.right:
                rect = self.get_embedded_window_rect()
                if rect:
                    left, top, right, bottom = rect

                    if left <= x <= right and top <= y <= bottom:
                        rel_x = x - left
                        rel_y = y - top
                        print(f"Relative click inside embedded app: ({rel_x}, {rel_y})")
                    else:
                        print("Click outside embedded app")
        
        listener = mouse.Listener(on_click=on_click)
        listener.start()


    def run(self):
        aurea_directory, aurea_name = self.select_folder_with_aurea()
        file_to_copy = "runExe.bat" 
        self.copy_file_to_folder(file_to_copy, aurea_directory)
        self.start_aurea(aurea_directory, file_to_copy)
        time.sleep(1)
        aurea_pid = self.get_aurea_pid(aurea_name)
        # self.wait_for_aurea_window(aurea_pid)
        self.embed_aurea(aurea_pid)    
        self.start_mouse_listener()

        

if __name__ == "__main__":
    app = AureaConnector()
    app.run()

        

