import ctypes
from pathlib import Path

class SensorUtils:

    @staticmethod
    def initialize() -> bool:
        p = Path(__file__).with_name('stream.dll')
        SensorUtils.lib = ctypes.CDLL(str(p.resolve())) 
        SensorUtils.lib.get_position.argtypes = [ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int)]
        SensorUtils.lib.get_position.restype = ctypes.c_int

        return SensorUtils.lib.gestic_setup() >= 0

    @staticmethod
    def get_position() -> tuple[int, int, int]:
        """Get the position of the hand above the sensor."""
        x = ctypes.c_int()
        y = ctypes.c_int()
        z = ctypes.c_int()

        SensorUtils.lib.get_position(ctypes.byref(x), ctypes.byref(y), ctypes.byref(z))

        return (x.value, y.value, z.value)

    @staticmethod
    def position_stream():
        """Generator that yields the (x, y, z) position indefinitely."""
        while True:
            yield SensorUtils.get_position()

    @staticmethod
    def unique_position_stream():
        """Generator that yields the (x, y, z) position only when it changes."""
        last_position = None
        while True:
            current_position = SensorUtils.get_position()
            if last_position != current_position:
                yield current_position
                last_position = current_position

    @staticmethod
    def shutdown():
        """Shutdown the library."""
        SensorUtils.lib.gestic_shutdown()

if __name__ == "__main__":
    import time
    if not SensorUtils.initialize():
        print("Could not open GestIC device.")
        exit(1)
    for x, y, z in SensorUtils.position_stream():
        print(f"X: {x}, Y: {y}, Z: {z}")
        time.sleep(0.1)