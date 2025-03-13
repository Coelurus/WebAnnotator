import unittest
import os
import sys
import cv2
from unittest.mock import patch, MagicMock, call

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "../..", "src", "utils")))

from video_utils import save_frames
from constants import TEMP_SAVE_PATH, REL_TEMP_SAVE_PATH, VIDEO_PATH

class TestVideoUtils(unittest.TestCase):

    @patch("video_utils.ensure_directory_exists")
    @patch("video_utils.cv2.VideoCapture")
    @patch("video_utils.cv2.imwrite")
    def test_save_frames(self, mock_imwrite, mock_VideoCapture, mock_ensure_directory_exists):
        # Mock the VideoCapture object and its methods
        mock_video_capture_instance = MagicMock()
        mock_VideoCapture.return_value = mock_video_capture_instance
        mock_video_capture_instance.read.return_value = (True, "frame_image")

        # Call the function with test timestamps
        timestamps = [1, 2, 3]
        save_frames(timestamps)

        # Ensure the directory exists
        mock_ensure_directory_exists.assert_called_once_with(REL_TEMP_SAVE_PATH)

        # Check if VideoCapture was called with the correct video path
        mock_VideoCapture.assert_called_with(VIDEO_PATH)

        # Check if set and read methods were called with correct parameters
        expected_calls = [call().set(cv2.CAP_PROP_POS_MSEC, t * 1000) for t in timestamps]
        mock_video_capture_instance.set.assert_has_calls(expected_calls, any_order=True)
        self.assertEqual(mock_video_capture_instance.read.call_count, len(timestamps))

        # Check if imwrite was called with correct parameters
        expected_imwrite_calls = [
            call(f".{TEMP_SAVE_PATH}frame_{int(1000 * t)}_msec.jpg", "frame_image")
            for t in timestamps
        ]
        mock_imwrite.assert_has_calls(expected_imwrite_calls, any_order=True)

if __name__ == "__main__":
    unittest.main()