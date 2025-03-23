package cz.cuni.mff.vopalenf.annotator.manager;

import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.dao.repository.AnnotationRepository;
import org.springframework.stereotype.Service;

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
@Service
public class DataLoaderManager {
    private static final String DATA_IDENTIFIER = "DATA";
    private static final int DATA_IDENTIFIER_IDX = 2;
    private static final String DEFAULT_LABEL = "left-right-wave";

    private final AnnotationRepository annotationRepository;

    public DataLoaderManager(AnnotationRepository annotationRepository) {
        this.annotationRepository = annotationRepository;
    }

    /**
     * Loads names of all log files from resources/sensor-data.
     * Just for dev purposes.
     *
     * @return array of Paths of all log files
     */
    public List<Path> loadFromResources() {
        try {
            URL logDirURL = DataLoaderManager.class.getClassLoader().getResource("sensor-data");
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

            return filePaths;
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
    public List<LogData> loadLogFile(Long projectId, Path filePath, Boolean withUnlabeled) {
        List<LogData> logDataList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            double lastTimestamp = -1.0;
            Long frameCount = 0L;

            while ((line = reader.readLine()) != null) {
                String[] logLine = line.split("\\s+");
                // Skip starting lines and not DATA lines
                if (logLine.length <= DATA_IDENTIFIER_IDX || !Objects.equals(logLine[DATA_IDENTIFIER_IDX], DATA_IDENTIFIER)) {
                    continue;
                }

                LogData newData = new LogData(DEFAULT_LABEL, line);
                // If there are multiple lines (data) for just one time moment, skip the second line, as frame will be just one
                if (lastTimestamp == newData.getTime()) {
                    continue;
                } else {
                    lastTimestamp = newData.getTime();
                }

                if (annotationRepository.existsByProjectIdAndFrameId(projectId, frameCount)) {
                    // TODO: change to real label name, not index
                    newData.setLabel(String.valueOf(annotationRepository.findByProjectIdAndFrameId(projectId, frameCount).getLabelId()));
                    logDataList.add(newData);
                } else if (withUnlabeled) {
                    logDataList.add(newData);
                }

                frameCount++;
            }

            return logDataList;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred");
        }

    }
}
