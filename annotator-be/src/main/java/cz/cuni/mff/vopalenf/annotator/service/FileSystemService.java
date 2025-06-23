package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.FrameCount;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.exception.api.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Service for accessing images of projects in file system
 */
@Service
public class FileSystemService {

    /**
     * Path to the file system where files are stored.
     * This value is read from application properties.
     */
    @Value("${app.file-system.path}") String fileSystemPath;
    /**
     * Extension of image files in the file system.
     * This value is read from application properties.
     */
    @Value("${app.file-system.image-extension}") String imageExtension;

    private final ProjectRepository projectRepository;

    /**
     * Constructor for FileSystemService.
     *
     * @param projectRepository the ProjectRepository instance to use for fetching ProjectEntities
     */
    @Autowired
    public FileSystemService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Get array of all image files for a project by its ID
     *
     * @param projectId ID of project to find images of
     * @return array of image files of project
     * @throws NotFoundException when projectId is invalid, or it does not have assigned directory
     */
    private File[] getImageFiles(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Invalid project id: " + projectId, FileSystemService.class.getSimpleName()));

        String logFileName = projectEntity.getLogFileName();
        Path pathToFS = Path.of(fileSystemPath);

        File projectDir = Arrays.stream(Objects.requireNonNull(pathToFS.toFile().listFiles()))
                .filter(file -> file.getName().equals(logFileName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Not found project dir for project with id: " + projectId, FileSystemService.class.getSimpleName()));

        return Objects.requireNonNull(projectDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(imageExtension))));
    }

    /**
     * Get frame from project at a position
     *
     * @param projectId ID of a project
     * @param position  Order number of the frame in project
     * @return image from a project at a position
     * @throws NotFoundException when projectId is invalid, or it does not have assigned directory
     * @throws ServerException   when image fetching fails
     */
    public Resource getFrame(Long projectId, Integer position) {
        File[] imageFiles = getImageFiles(projectId);
        Arrays.sort(imageFiles);
        Resource resource;

        if (imageFiles.length <= position) {
            throw new NotFoundException(
                    "Position " + position + " of image for project with id " + projectId + "is out of range",
                    FileSystemService.class.getSimpleName()
            );
        }

        try {
            resource = new UrlResource(Path.of(imageFiles[position].getPath()).toUri());
        } catch (MalformedURLException e) {
            throw new ServerException("Fetching image failed", FileSystemService.class.getSimpleName());
        }
        return resource;
    }

    /**
     * Get number of frames of a project by its ID
     *
     * @param projectId ID of project to count its images
     * @return Frame count
     * @throws NotFoundException when projectId is invalid, or it does not have assigned directory
     */
    public FrameCount getFramesCount(Long projectId) {
        return FrameCount.builder()
                .count(getImageFiles(projectId).length)
                .build();

    }
}
