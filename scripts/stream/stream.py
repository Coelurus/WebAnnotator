import ctypes
from datetime import datetime
import time

lib = ctypes.CDLL('./stream.dll') 

lib.get_position.argtypes = [ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int)]
lib.get_position.restype = ctypes.c_int

if lib.gestic_setup() < 0:
    print("Could not open GestIC device.")
    exit(1)

last_x = last_y = last_z = None

try:
    while True:
        x = ctypes.c_int()
        y = ctypes.c_int()
        z = ctypes.c_int()

        lib.get_position(ctypes.byref(x), ctypes.byref(y), ctypes.byref(z))

        if (x.value, y.value, z.value) != (last_x, last_y, last_z):
            now = datetime.now().strftime('%H:%M:%S.%f')[:-3]
            print(f"{now}\t{x.value}\t{y.value}\t{z.value}")
            last_x, last_y, last_z = x.value, y.value, z.value
        time.sleep(0.001)

finally:
    lib.gestic_shutdown()
