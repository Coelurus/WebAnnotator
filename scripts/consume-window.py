import sys
import time
import psutil
import win32gui
import win32con
import win32process
from PyQt5.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QWidget, QPushButton, QMessageBox


def find_window_by_pid(pid):
    """Find the top-level window associated with a given process ID."""
    def callback(hwnd, results):
        _, found_pid = win32process.GetWindowThreadProcessId(hwnd)
        if found_pid == pid and win32gui.IsWindowVisible(hwnd):
            results.append(hwnd)
        return True

    windows = []
    win32gui.EnumWindows(callback, windows)
    return windows[0] if windows else None


def find_aurea_window():
    """Find the Aurea window based on process name."""
    def find_process_pid(process_name):
        """Find process ID based on its name."""
        for proc in psutil.process_iter(['pid', 'name']):
            if process_name.lower() in proc.info['name'].lower():
                return proc.info['pid']
        return None

    pid = find_process_pid("Aurea.exe")
    if pid:
        hwnd = find_window_by_pid(pid)
        return hwnd
    return None


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Embed Aurea App")
        self.setGeometry(100, 100, 800, 800)
        
        # Initialize attributes
        self.aurea_hwnd = None

        # Create a central widget
        central_widget = QWidget(self)
        layout = QVBoxLayout(central_widget)
        self.setCentralWidget(central_widget)

        # Create the Embed button
        self.embed_button = QPushButton("Embed Aurea", self)
        self.embed_button.clicked.connect(self.embed_aurea)
        layout.addWidget(self.embed_button)

    def embed_aurea(self):
        """Embed the Aurea app window into the PyQt window."""
        try:
            hwnd = find_aurea_window()
            if not hwnd:
                QMessageBox.warning(self, "Error", "Aurea window not found!")
                return

            self.aurea_hwnd = hwnd
            self.reparent_window()
        except Exception as e:
            QMessageBox.critical(self, "Error", f"Failed to embed Aurea:\n{str(e)}")

    def reparent_window(self):
        """Reparent the Aurea window to the PyQt application."""
        if not self.aurea_hwnd or not win32gui.IsWindow(self.aurea_hwnd):
            QMessageBox.warning(self, "Error", "Invalid Aurea window handle!")
            return

        # Reparent the Aurea window to this PyQt widget
        win32gui.SetParent(self.aurea_hwnd, int(self.centralWidget().winId()))

        # Adjust Aurea window style to fit the container
        style = win32gui.GetWindowLong(self.aurea_hwnd, win32con.GWL_STYLE)
        win32gui.SetWindowLong(self.aurea_hwnd, win32con.GWL_STYLE, (style & ~win32con.WS_POPUP) | win32con.WS_CHILD)
        win32gui.SetWindowPos(
            self.aurea_hwnd,
            None,
            0, 0, self.width(), self.height(),
            win32con.SWP_FRAMECHANGED | win32con.SWP_NOZORDER | win32con.SWP_NOACTIVATE
        )

    def resizeEvent(self, event):
        """Resize the embedded Aurea window when the main window is resized."""
        if self.aurea_hwnd:
            win32gui.SetWindowPos(
                self.aurea_hwnd,
                None,
                0, 0, self.width(), self.height(),
                win32con.SWP_NOZORDER | win32con.SWP_NOACTIVATE
            )
        super().resizeEvent(event)


if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())
