package cz.cuni.mff.vopalenf.filesystemmanager.service;

import cz.cuni.mff.vopalenf.constants.Constants;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

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

    public String getFrame(@PathVariable Long id, @PathVariable int position) {
        File[] imageFiles = getImageFiles(id);
        Arrays.sort(imageFiles);
        Arrays.sort(imageFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getPath().substring(f.getPath().indexOf("frame_") + 6, f.getPath().indexOf("_msec.jpg")))));
        return "<img src='\\" + imageFiles[position].getPath() + "' alt='annotation preview' width='50px' " +
                "height='50px'/>";
    }

    public int getFramesCount(Long projectId) {
        return getImageFiles(projectId).length;

    }
}
