import cv2

cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)

if not cap.isOpened():
    print("Error: Could not open camera.")
else:
    print("Camera opened successfully.")
input("Click to end...")
# cap.release()