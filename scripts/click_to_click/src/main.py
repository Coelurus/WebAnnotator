import time
from pynput import mouse
from utils.video_utils import on_click, record_video, save_frames
from utils.file_utils import get_timestamps, zip_files, clean_temp

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
    print("Creating ZIP file")
    zip_files(file_name)
    print("Cleaning debris")
    clean_temp()
    print("Getting data was successful")