import ctypes
import numpy as np
import pyqtgraph as pg
from PyQt5 import QtCore, QtWidgets

# Initialize graph
app = pg.mkQApp("Gesture Visualization")

# Create the main window
win = QtWidgets.QMainWindow()
win.setWindowTitle('Gesture Tracking')
#win.resize(1200, 600)

# Create a central widget and layout
central_widget = QtWidgets.QWidget()
win.setCentralWidget(central_widget)
layout = QtWidgets.QHBoxLayout(central_widget)

# Create front view
frontView = pg.GraphicsLayoutWidget()
layout.addWidget(frontView)
frontPlot = frontView.addPlot()
frontPlot.setLabel('left', 'West')
frontPlot.setLabel('right', 'East')
frontPlot.setLabel('bottom', 'South')
frontPlot.setLabel('top', 'North')
frontPlot.setTitle('Front View')
frontPlot.setXRange(0, 65535)
frontPlot.setYRange(0, 65535)
frontPlot.setLimits(xMin=0, xMax=65535, yMin=0, yMax=65535)
frontPlot.enableAutoRange(False, False)
# Set the physical aspect ratio to 95:60
frontView.setFixedSize(950, 600)
# Disable mouse interactions
frontPlot.setMouseEnabled(x=False, y=False)
frontPlot.hideButtons()
frontPlot.setMenuEnabled(False)

# Create top view
topView = pg.GraphicsLayoutWidget()
layout.addWidget(topView)
topPlot = topView.addPlot()
topPlot.setLabel('left', 'West')
topPlot.setLabel('right', 'East')
topPlot.setLabel('bottom', 'South')
topPlot.setLabel('top', 'North')
topPlot.setTitle('Top View')
topPlot.setXRange(0, 65535)
topPlot.setYRange(0, 65535)
topPlot.setLimits(xMin=0, xMax=65535, yMin=0, yMax=65535)
topPlot.enableAutoRange(False, False)
# Set the physical aspect ratio to 95:60
topView.setFixedSize(950, 600)
# Disable mouse interactions
topPlot.setMouseEnabled(x=False, y=False)
topPlot.hideButtons()
topPlot.setMenuEnabled(False)

# Create scatter plot items
frontScatter = pg.ScatterPlotItem(pen=None, symbol='o', size=20, brush='r')
topScatter = pg.ScatterPlotItem(pen=None, symbol='o', size=20, brush='r')
frontPlot.addItem(frontScatter)
topPlot.addItem(topScatter)

# Load the DLL
lib = ctypes.CDLL('./stream.dll') 
lib.get_position.argtypes = [ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int)]
lib.get_position.restype = ctypes.c_int

if lib.gestic_setup() < 0:
    print("Could not open GestIC device.")
    exit(1)

def update():
    x = ctypes.c_int()
    y = ctypes.c_int()
    z = ctypes.c_int()

    lib.get_position(ctypes.byref(x), ctypes.byref(y), ctypes.byref(z))
    
    # Update front scatter
    frontScatter.setData([x.value], [z.value])
    
    # Update top scatter
    topScatter.setData([x.value], [y.value])

# Create a timer to update the plot
timer = QtCore.QTimer()
timer.timeout.connect(update)
# Update every 1ms
timer.start(1)  

win.show()

try:
    app.exec_()
finally:
    lib.gestic_shutdown()
