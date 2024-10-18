package cz.cuni.mff.vopalenf.datamanager.service;

import cz.cuni.mff.vopalenf.constants.Constants;
import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Annotation;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.ProjectPriority;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.repositories.AnnotationRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.response.ProjectResponse;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final StorageService storageService;
    private final AnnotationRepository annotationRepository;

    private static final String DEFAULT = "default";

    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          StorageService storageService,
                          AnnotationRepository annotationRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageService = storageService;
        this.annotationRepository = annotationRepository;
    }

    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll().stream()
                .map(project -> new ProjectResponse(project, Views.ShowTeamsInUsers.class))
                .toList()
        );
    }

    public ResponseEntity<ProjectResponse> getProject(Long projectId) {
        return ResponseEntity.ok(new ProjectResponse(
                projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("NOT FOUND")),
                Views.ShowTeamsInUsers.class));
    }

    public ResponseEntity<List<ProjectPriority>> getAllProjectPriorities() {
        return ResponseEntity.ok(Arrays.stream(ProjectPriority.class.getEnumConstants()).toList());
    }

    public String manageFileUpload(String projectName,LocalDate deadline,
            String priority, Integer teamId, MultipartFile file) {

        Team team = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        String compressedFileName = file.getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName)
                .substring(0, compressedFileName.indexOf(Constants.ARCHIVE_EXTENSION));
        // TODO: fix priorities ... make functional enum
        Project project = new Project(projectName, shortenedFileName, deadline, 1, team);
        projectRepository.save(project);
        storageService.store(file);
        // TODO: better resolving of success also on frontend
        return "OK";
    }

    public ResponseEntity<Void> annotateProjectFrame(Long projectId, Long frameId) {
        if (annotationRepository.existsByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT)) {
            annotationRepository.deleteByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT);
        } else {
            annotationRepository.save(new Annotation(projectId, frameId, DEFAULT));
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<Annotation>> getAllAnnotations (Long projectId) {
        return ResponseEntity.ok(annotationRepository.findByProjectId(projectId));
    }
}
