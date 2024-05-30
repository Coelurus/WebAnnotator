import cv2
from pynput import mouse

recording = False
out = None
running = True


def on_click(x, y, button, pressed):
    global recording, out, running

    if button == mouse.Button.left and pressed:
        recording = not recording
        if recording:
            print("Recording started")
            fourcc = cv2.VideoWriter_fourcc(*"XVID")
            out = cv2.VideoWriter("output.avi", fourcc, 20.0, (640, 480))
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


if __name__ == "__main__":
    listener = mouse.Listener(on_click=on_click)
    listener.start()

    record_video()

    listener.stop()
    listener.join()
