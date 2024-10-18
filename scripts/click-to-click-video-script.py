import os
import time
from zipfile import ZipFile, ZIP_DEFLATED

# pip install pynput
from pynput import mouse

# pip install PyAutoGUI
import pyautogui

# pip install pyperclip
import pyperclip

# pip install opencv-python
import cv2

### pip install pynput PyAutoGUI pyperclip opencv-python

recording = False
out = None
running = True

TEMP_SAVE_PATH = "/scripts/temp/"
REL_TEMP_SAVE_PATH = "." + TEMP_SAVE_PATH
VIDEO_PATH = ".\\scripts\\temp\\video.avi"
OUT_PATH = ".\\scripts\\out"


def on_click(x, y, button, pressed):
    global recording, out, running

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
            out = cv2.VideoWriter(VIDEO_PATH, fourcc, 20.0, (640, 480))
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


def get_timestamps() -> tuple[set[float], str]:
    timestamps = set()
    for file in os.listdir(REL_TEMP_SAVE_PATH):
        if file.endswith(".log"):
            with open(os.path.join(REL_TEMP_SAVE_PATH, file)) as my_file:
                for line in my_file:
                    line_fragments = line.split("\t")
                    if len(line_fragments) == 0 or not is_float(line_fragments[0]):
                        continue
                    timestamps.add(float(line_fragments[0]))
                return timestamps, file


def save_frames(timestamps):
    for timestamp in timestamps:
        video_capture = cv2.VideoCapture(VIDEO_PATH)
        video_capture.set(cv2.CAP_PROP_POS_MSEC, timestamp * 1000)
        success, image = video_capture.read()
        if success:
            cv2.imwrite(
                "."
                + TEMP_SAVE_PATH
                + "frame_"
                + str(int(1000 * timestamp))
                + "_msec.jpg",
                image,
            )


def zip_files(file_name: str):
    with ZipFile(
        os.path.join(OUT_PATH, file_name.replace(".log", ".zip")), "w", ZIP_DEFLATED
    ) as zip:
        for file in os.listdir(REL_TEMP_SAVE_PATH):
            if file.endswith(".jpg") or file.endswith(".log"):
                zip.write(os.path.join(REL_TEMP_SAVE_PATH, file), arcname=file)


def clean_temp():
    for file in os.listdir(REL_TEMP_SAVE_PATH):
        os.remove(os.path.join(REL_TEMP_SAVE_PATH, file))


if __name__ == "__main__":
    print("Recording will start on button press. Use it on REC button in Aurea app")
    listener = mouse.Listener(on_click=on_click)
    listener.start()

    record_video()

    listener.stop()
    listener.join()

    print("Video and log file created")
    time.sleep(3)

    print("Loading timestamps from log file")
    timestamps, file_name = get_timestamps()
    print("Extracting matching frames")
    save_frames(timestamps)
    print("Creatin ZIP file")
    zip_files(file_name)
    print("Cleaning debris")
    clean_temp()
    print("Getting data was successful")
