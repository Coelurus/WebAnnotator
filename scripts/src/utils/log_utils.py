
from io import TextIOWrapper
from typing import List
import csv
from .constants import  TEMP_SAVE_PATH, REL_TEMP_SAVE_PATH

class LogFileParser:

    def __init__(self, log_file_path, csv_file_path):
        self.log_file_path = log_file_path
        self.csv_file_path = csv_file_path
        self.timestamps = set()

    def parse(self):
        print(self.log_file_path)
        with open(self.log_file_path, 'r') as log_file:
            with open(self.csv_file_path, 'w', newline='') as csv_file:
                csv_writer = csv.writer(csv_file, delimiter=',')
                self.get_headers(log_file, csv_writer)
                self.get_data(log_file, csv_writer)

    def get_headers(self, log_file: TextIOWrapper, csv_file):
        while True:
            line = log_file.readline()
            if not line:
                break
            if line == "\n":
                continue
            if line.startswith("TIME"):
                lineParts = line.strip().split("\t")
                if lineParts.count("3D DATA") == 1:
                    csv_file.writerow(
                        [lineParts[0].replace(" ", "_")] + 
                        [x.replace(" ", "_") for x in lineParts[7:-5]] + 
                        ["IMAGE"])
                    break

    def get_data(self, log_file: TextIOWrapper, csv_file):
        while True:
            line = log_file.readline()
            if not line:
                break
            if line == "\n":
                continue

            lineParts = line.strip().split("\t")
            if lineParts.count("3D DATA") == 1:
                csv_file.writerow(
                    [lineParts[0]] + 
                    lineParts[7:-5] +
                    [f"frame_{int(1000 * float(lineParts[0]))}_msec.jpg"])
                self.timestamps.add(float(lineParts[0]))
                    
    def get_timestamps(self) -> set[float]:
        """Extract timestamps from log files.""" 
        return self.timestamps

if __name__ == "__main__":
    log_file_parser = LogFileParser(REL_TEMP_SAVE_PATH + "gestic_20250312_182248.log", REL_TEMP_SAVE_PATH + "output.csv")
    log_file_parser.parse()