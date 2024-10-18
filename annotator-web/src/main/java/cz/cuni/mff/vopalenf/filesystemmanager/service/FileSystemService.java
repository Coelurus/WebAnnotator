package cz.cuni.mff.vopalenf.filesystemmanager.service;

import cz.cuni.mff.vopalenf.constants.Constants;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;

import static org.apache.tomcat.util.http.fileupload.IOUtils.*;

@Service
public class FileSystemService {

    private final ProjectRepository projectRepository;

    @Autowired
    public FileSystemService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private File[] getImageFiles(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));
        String logFileName = project.getLogFileName();
        Path pathToFS = Path.of(Constants.FILE_SYSTEM_PATH);

        File projectDir = Arrays.stream(Objects.requireNonNull(pathToFS.toFile().listFiles()))
                .filter(file -> file.getName().equals(logFileName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));


        return Objects.requireNonNull(projectDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(".jpg"))));
    }

    public ResponseEntity<Resource> getFrame(@PathVariable Long id, @PathVariable int position) {
        File[] imageFiles = getImageFiles(id);
        Arrays.sort(imageFiles);
        Arrays.sort(imageFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getPath().substring(f.getPath().indexOf("frame_") + 6, f.getPath().indexOf("_msec.jpg")))));
        Resource resource = null;
        try {
            resource = new UrlResource(Path.of(imageFiles[position].getPath()).toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    public ResponseEntity<Map<String, Integer>> getFramesCount(Long projectId) {
        Map<String, Integer> countObject = new HashMap<>();
        countObject.put("count", getImageFiles(projectId).length);
        return ResponseEntity.ok(countObject);

    }
}
