package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.constants.Constants;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileSystemService {

    private final ProjectRepository projectRepository;

    @Autowired
    public FileSystemService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private File[] getImageFiles(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));
        String logFileName = projectEntity.getLogFileName();
        Path pathToFS = Path.of(Constants.FILE_SYSTEM_PATH);

        File projectDir = Arrays.stream(Objects.requireNonNull(pathToFS.toFile().listFiles()))
                .filter(file -> file.getName().equals(logFileName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));


        return Objects.requireNonNull(projectDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(".jpg"))));
    }

    public ResponseEntity<Resource> getFrame(Long id, Integer position) {
        File[] imageFiles = getImageFiles(id);
        Arrays.sort(imageFiles);
        Arrays.sort(imageFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getPath().substring(f.getPath().indexOf("frame_") + 6, f.getPath().indexOf("_msec.jpg")))));
        Resource resource = null;
        try {
            resource = new UrlResource(Path.of(imageFiles[position].getPath()).toUri());
        } catch (MalformedURLException e) {
            throw new ServerException("Fetching image failed", FileSystemService.class.getSimpleName());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    public ResponseEntity<List<Resource>> getFrames(Long id, Integer fromPosition, Integer toPosition) {
        File[] imageFiles = getImageFiles(id);
        Arrays.sort(imageFiles);
        Arrays.sort(imageFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getPath().substring(f.getPath().indexOf("frame_") + 6, f.getPath().indexOf("_msec.jpg")))));
        List<Resource> resources = new ArrayList<>();
        try {
            for (int i = fromPosition; i < toPosition; i++) {
                resources.add(new UrlResource(Path.of(imageFiles[i].getPath()).toUri()));
            }
        } catch (MalformedURLException e) {
            throw new ServerException("Fetching images failed", FileSystemService.class.getSimpleName());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resources);
    }

    public ResponseEntity<Map<String, Integer>> getFramesCount(Long projectId) {
        Map<String, Integer> countObject = new HashMap<>();
        countObject.put("count", getImageFiles(projectId).length);
        return ResponseEntity.ok(countObject);

    }
}
