import os
import sys
import unittest
from zipfile import ZIP_DEFLATED
from unittest.mock import patch, mock_open, MagicMock

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "../..", "src", "utils")))
from file_utils import is_float, ensure_directory_exists, get_timestamps, zip_files, clean_temp
from constants import REL_TEMP_SAVE_PATH, OUT_PATH

class TestFileUtils(unittest.TestCase):

    def test_is_float(self):
        self.assertTrue(is_float("123.456"))
        self.assertTrue(is_float("0.0"))
        self.assertFalse(is_float("abc"))
        self.assertFalse(is_float("123.abc"))

    @patch("os.makedirs")
    @patch("os.path.exists", return_value=False)
    def test_ensure_directory_exists(self, mock_exists, mock_makedirs):
        ensure_directory_exists("some/path")
        mock_exists.assert_called_once_with("some/path")
        mock_makedirs.assert_called_once_with("some/path")

    @patch("os.listdir", return_value=["file1.log", "image1.jpg"])
    @patch("builtins.open", 
           new_callable=mock_open, 
           read_data=
           """Aurea 3.0.04
            VersionInfo: 3D Bridge, I2C Bridge 2.21
            VersionInfo: 3D Customer String, - empty -
            VersionInfo: 3D FW Version, 3.0.04 ID4400r3973
            VersionInfo: 3D Parameter ID, 0
            VersionInfo: 3D Platform, MGC3140 (48 pins, multi-TX)

            TIME (s)	3D DATA	Running	fTx	Pos x	Pos y	Pos z	CIC S	CIC W	CIC N	CIC E	CIC C	SD S	SD W	SD N	SD E	SD C	Touch	Tap	DblTap	AirWheel	Gesture
            TIME (s)	3D MESSAGE	Byte 1	Byte 2	...

            TIME (s)	2D DATA	TxFreq	TxVolt	AVG	MsgType	Values
            TIME (s)	2D MESSAGE	MsgID	Payload

            0.024	3D MESSAGE	48	00	F4	91	20	59	68	8C	32	8D	A8	3C	08	07	97	C7	9A	DE	9E	C7	B7	81	99	C7	8A	53	A2	C7	39	65	BC	C7	00	00	B5	3F	00	00	04	40	00	00	A2	3F	00	00	83	3F	00	80	5B	40	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00
            0.024	3D DATA	true	-	-	-	-	-77326.1	-81341.2	-78595.4	-83111.1	-96458.4	1.4	2.1	1.3	1.0	3.4	-	-	-	-	-
            0.025	3D MESSAGE	48	00	F5	91	20	59	69	8C	32	8D	A8	3C	18	07	97	C7	A6	DE	9E	C7	B4	81	99	C7	85	53	A2	C7	42	65	BC	C7	00	00	A5	3F	00	00	FC	3F	00	00	A5	3F	00	00	88	3F	00	00	57	40	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00
            0.025	3D DATA	true	-	-	-	-	-77326.2	-81341.3	-78595.4	-83111.0	-96458.5	1.3	2.0	1.3	1.1	3.4	-	-	-	-	-
            0.025	3D MESSAGE	48	00	F6	91	20	59	6A	8C	32	8D	A8	3C	25	07	97	C7	AA	DE	9E	C7	B2	81	99	C7	86	53	A2	C7	50	65	BC	C7	00	00	98	3F	00	00	F8	3F	00	00	A7	3F	00	00	87	3F	00	00	50	40	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00
            0.025	3D DATA	true	-	-	-	-	-77326.3	-81341.3	-78595.4	-83111.0	-96458.6	1.2	1.9	1.3	1.1	3.2	-	-	-	-	-
            0.025	3D MESSAGE	48	00	F7	91	20	59	6B	8C	32	8D	A8	3C	2A	07	97	C7	A8	DE	9E	C7	AE	81	99	C7	8B	53	A2	C7	64	65	BC	C7	00	00	93	3F	00	00	FA	3F	00	00	AB	3F	00	00	82	3F	00	00	46	40	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00	00
            0.025	3D DATA	true	-	-	-	-	-77326.3	-81341.3	-78595.4	-83111.1	-96458.8	1.1	2.0	1.3	1.0	3.1	-	-	-	-	-
            """)
    def test_get_timestamps(self, mock_open, mock_listdir):
        timestamps, file = get_timestamps()
        self.assertEqual(timestamps, {0.024, 0.025})
        self.assertEqual(file, "file1.log")

    @patch("os.listdir", return_value=["file1.log", "image.jpg"])
    @patch("file_utils.ZipFile")
    @patch("file_utils.ensure_directory_exists")
    def test_zip_files(self, mock_ensure_dir, mock_zipfile, mock_listdir):
        mock_zip_instance = MagicMock()
        mock_zipfile.return_value.__enter__.return_value = mock_zip_instance

        zip_files("file1.log")

        mock_ensure_dir.assert_called_once_with(OUT_PATH)
        mock_zipfile.assert_called_once_with(os.path.join(OUT_PATH, "file1.zip"), "w", ZIP_DEFLATED)
        mock_zip_instance.write.assert_any_call(os.path.join(REL_TEMP_SAVE_PATH, "file1.log"), arcname="file1.log")
        mock_zip_instance.write.assert_any_call(os.path.join(REL_TEMP_SAVE_PATH, "image.jpg"), arcname="image.jpg")

    @patch("os.remove")
    @patch("os.listdir", return_value=["file1.log", "image.jpg"])
    def test_clean_temp(self, mock_listdir, mock_remove):
        clean_temp()
        mock_listdir.assert_called_once_with(REL_TEMP_SAVE_PATH)
        mock_remove.assert_any_call(os.path.join(REL_TEMP_SAVE_PATH, "file1.log"))
        mock_remove.assert_any_call(os.path.join(REL_TEMP_SAVE_PATH, "image.jpg"))

if __name__ == "__main__":
    unittest.main()