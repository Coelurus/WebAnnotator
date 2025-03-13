import os
from zipfile import ZipFile, ZIP_DEFLATED
from .constants import REL_TEMP_SAVE_PATH, OUT_PATH

def is_float(string):
    """Check if a string can be converted to a float."""
    try:
        float(string)
        return True
    except ValueError:
        return False


def ensure_directory_exists(path: str):
    """Ensure a directory exists, create it if not."""
    if not os.path.exists(path):
        os.makedirs(path)

def get_timestamps() -> tuple[set[float], str]:
    """Extract timestamps from log files.""" 

    timestamps = set()
    for file in os.listdir(REL_TEMP_SAVE_PATH):
        if file.endswith(".log"):
            with open(os.path.join(REL_TEMP_SAVE_PATH, file)) as my_file:
                for line in my_file:
                    line_fragments = line.split("\t")
                    if len(line_fragments) == 0 or not is_float(line_fragments[0]):
                        continue
                    timestamps.add(float(line_fragments[0]))
                return timestamps, file
    return set(), ""


def zip_files(file_name: str):
    """Zip relevant files into an archive."""
    ensure_directory_exists(OUT_PATH)

    zip_path = os.path.join(OUT_PATH, file_name.replace(".log", ".zip"))
    with ZipFile(zip_path, "w", ZIP_DEFLATED) as zipf:
        for file in os.listdir(REL_TEMP_SAVE_PATH):
            if file.endswith(".jpg") or file.endswith(".log"):
                zipf.write(os.path.join(REL_TEMP_SAVE_PATH, file), arcname=file)


def clean_temp():
    """Remove temporary files."""
    for file in os.listdir(REL_TEMP_SAVE_PATH):
         os.remove(os.path.join(REL_TEMP_SAVE_PATH, file))