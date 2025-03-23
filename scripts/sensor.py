import time
import csv

# pip install smbus2

import serial.tools.list_ports

ports = serial.tools.list_ports.comports()
for port in ports:
    print(port.device, port.description)

import serial
import time

# Change COM port to match your setup
COM_PORT = "COM4"  # Adjust based on Device Manager output

try:
    # Open the serial connection
    ser = serial.Serial(COM_PORT,  timeout=1)
    print(f"Connected to {COM_PORT}")

    payload = bytes([0x0A, 0x00, 0x00, 0x40, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06])

    # Send the request
    ser.write(payload)
    print("Firmware version request sent...")

    # Read response
    response = ser.readline()  # Read up to 64 bytes
    print("Response received:", response.hex())

except serial.SerialException as e:
    print(f"Error: {e}")
finally:
    if 'ser' in locals():
        ser.close()