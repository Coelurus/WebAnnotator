import os
from zipfile import ZipFile, ZIP_DEFLATED

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

def get_log_file_name(path: str) -> str:
    """Extract timestamps from log files.""" 

    for file in os.listdir(path):
        if file.endswith(".log"):
            return file
    return None

def zip_files(file_name: str, out_path: str, rel_temp_path: str):
    """Zip relevant files into an archive."""
    ensure_directory_exists(out_path)

    zip_path = os.path.join(out_path, file_name + ".zip")
    with ZipFile(zip_path, "w", ZIP_DEFLATED) as zipf:
        for file in os.listdir(rel_temp_path):
            if file.endswith(".webp") or file.endswith(".png") or file.endswith(".jpg") or file.endswith(".log") or file.endswith(".csv"):
                zipf.write(os.path.join(rel_temp_path, file), arcname=file)


def clean_folder(path: str):
    """Remove temporary files."""
    for file in os.listdir(path):
         os.remove(os.path.join(path, file))