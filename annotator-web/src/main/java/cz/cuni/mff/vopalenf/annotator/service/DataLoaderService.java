package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.LogData;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to file manage and load log data from sensor.
 */
public class DataLoaderService {
    private final String DATA_IDENTIFIER = "DATA";
    private final int DATA_IDENTIFIER_IDX = 2;

    /**
     * Loads names of all log files from resources/sensor-data.
     * Just for dev purposes.
     *
     * @return array of Paths of all log files
     */
    public Path[] loadFromResources() {
        try {
            URL logDirURL = DataLoaderService.class.getClassLoader().getResource("sensor-data");
            if (logDirURL == null) {
                throw new RuntimeException("Resource directory 'sensor-data' not found.");
            }
            Path logDirPath = Paths.get(logDirURL.toURI());

            List<Path> filePaths = new ArrayList<>();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(logDirPath)) {
                for (Path path : directoryStream) {
                    if (Files.isRegularFile(path)) {
                        filePaths.add(path);
                    }
                }
            }

            return filePaths.toArray(new Path[0]);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem with parsing URI");
        } catch (IOException e) {
            throw new RuntimeException("Problem with loading files from directory");
        }
    }

    /**
     * Loads all data capturing from one log file.
     *
     * @param filePath Path to a log file containing data from sensor from one capturing
     * @return Array of <code>LogData</code> containing information about every captured moment
     */
    public LogData[] loadLogFile(Path filePath) {
        List<LogData> logDataList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] logLine = line.split("\\s+");
                if (logLine.length <= DATA_IDENTIFIER_IDX || !Objects.equals(logLine[DATA_IDENTIFIER_IDX], DATA_IDENTIFIER)) {
                    continue;
                }
                logDataList.add(new LogData(line));
            }

            return logDataList.toArray(new LogData[0]);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred");
        }

    }
}
