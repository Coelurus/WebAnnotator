import os
import cv2
import time
import pyperclip
import pyautogui
from pynput import mouse

recording = False
out = None
running = True

TEMP_SAVE_PATH = "/scripts/out/"


def on_click(x, y, button, pressed):
    global recording, out, running

    if button == mouse.Button.left and pressed:
        recording = not recording
        if recording:
            time.sleep(1)
            pyperclip.copy(os.getcwd())

            pyautogui.press("home")
            pyautogui.hotkey("ctrl", "v")
            pyautogui.write(TEMP_SAVE_PATH)
            pyautogui.press("enter")

            print("Recording started")
            fourcc = cv2.VideoWriter_fourcc(*"XVID")
            out = cv2.VideoWriter(
                ".\\scripts\\out\\video.avi", fourcc, 20.0, (640, 480)
            )
        else:
            print("Recording stopped")
            out.release()
            out = None
            running = False


def record_video():
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


def is_float(string):
    try:
        float(string)
        return True
    except ValueError:
        return False


def get_timestamps():
    timestamps = set()
    for file in os.listdir("." + TEMP_SAVE_PATH):
        if file.endswith(".log"):
            with open("." + TEMP_SAVE_PATH + file) as my_file:
                for line in my_file:
                    line_fragments = line.split("\t")
                    if len(line_fragments) == 0 or not is_float(line_fragments[0]):
                        continue
                    timestamps.add(float(line_fragments[0]))
                return timestamps


if __name__ == "__main__":
    """
    listener = mouse.Listener(on_click=on_click)
    listener.start()

    record_video()

    listener.stop()
    listener.join()
    """

    timestamps = get_timestamps()
    print(timestamps)
