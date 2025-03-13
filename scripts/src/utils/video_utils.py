import os
import time
import cv2
import pyautogui
import pyperclip
from pynput import mouse

from .file_utils import ensure_directory_exists
from .constants import IMAGE_HEIGHT, IMAGE_WIDTH, TEMP_SAVE_PATH, REL_TEMP_SAVE_PATH, VIDEO_PATH

recording = False
out = None
running = True

def on_click(x, y, button, pressed):
    """Handles mouse click events to start/stop recording."""
    global recording, out, running

    ensure_directory_exists(REL_TEMP_SAVE_PATH)

    if button == mouse.Button.left and pressed:
        recording = not recording
        if recording:
            time.sleep(3)
            pyperclip.copy(os.getcwd())

            pyautogui.press("home")
            pyautogui.hotkey("ctrl", "v")
            pyautogui.write(TEMP_SAVE_PATH)
            time.sleep(1)
            pyautogui.press("enter")

            print("Recording started")
            fourcc = cv2.VideoWriter_fourcc(*"XVID")
            out = cv2.VideoWriter(VIDEO_PATH, fourcc, 20.0, (IMAGE_WIDTH, IMAGE_HEIGHT))
        else:
            print("Recording stopped")
            out.release()
            out = None
            running = False

def record_video():
    """Starts the webcam and records video when enabled."""
    global out, recording, running

    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("Error: Could not open camera.")
        return

    cv2.namedWindow("Camera")

    while running:
        ret, frame = cap.read()

        if not ret:
            print("Error: Could not read frame.")
            break

        if recording and out is not None:
            out.write(frame)

        cv2.imshow("Camera", frame)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            break

    cap.release()
    if out is not None:
        out.release()
    cv2.destroyAllWindows()


def save_frames(timestamps):
    """Saves frames at specific timestamps."""
    ensure_directory_exists(REL_TEMP_SAVE_PATH)

    for timestamp in timestamps:
        video_capture = cv2.VideoCapture(VIDEO_PATH)
        video_capture.set(cv2.CAP_PROP_POS_MSEC, timestamp * 1000)
        success, image = video_capture.read()
        if success:
            cv2.imwrite(
                f".{TEMP_SAVE_PATH}frame_{int(1000 * timestamp)}_msec.jpg", image
            )