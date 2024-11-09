package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.constants.Constants;
import cz.cuni.mff.vopalenf.annotator.dao.model.*;
import cz.cuni.mff.vopalenf.annotator.enums.ProjectPriority;
import cz.cuni.mff.vopalenf.annotator.storage.StorageManager;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.AnnotationRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.LabelRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectResponse;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final StorageManager storageManager;
    private final AnnotationRepository annotationRepository;
    private final LabelRepository labelRepository;

    private static final String DEFAULT = "default";

    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          StorageManager storageManager,
                          AnnotationRepository annotationRepository,
                          LabelRepository labelRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageManager = storageManager;
        this.annotationRepository = annotationRepository;
        this.labelRepository = labelRepository;
    }

    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll().stream()
                .map(projectEntity -> new ProjectResponse(projectEntity, Views.ShowTeamsInUsers.class))
                .toList()
        );
    }

    public ResponseEntity<ProjectResponse> getProject(Long projectId) {
        return ResponseEntity.ok(new ProjectResponse(
                //TODO: implement own exception handling
                projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("NOT FOUND")),
                Views.ShowTeamsInUsers.class));
    }

    public ResponseEntity<List<ProjectPriority>> getAllProjectPriorities() {
        return ResponseEntity.ok(Arrays.stream(ProjectPriority.class.getEnumConstants()).toList());
    }

    public ResponseEntity<String> manageFileUpload(String projectName,LocalDate deadline,
            String priority, Integer teamId, MultipartFile file) {

        TeamEntity teamEntity = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        String compressedFileName = file.getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName)
                .substring(0, compressedFileName.indexOf(Constants.ARCHIVE_EXTENSION));
        // TODO: fix priorities ... make functional enum
        ProjectEntity projectEntity = new ProjectEntity(projectName, shortenedFileName, deadline, 1, teamEntity);
        projectRepository.save(projectEntity);
        storageManager.store(file);
        // TODO: better resolving of success also on frontend
        return ResponseEntity.ok("OK");
    }

    public ResponseEntity<Void> annotateProjectFrame(Long projectId, Long frameId) {
        if (annotationRepository.existsByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT)) {
            annotationRepository.deleteByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT);
        } else {
            annotationRepository.save(new AnnotationEntity(projectId, frameId, DEFAULT));
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<AnnotationEntity>> getAllAnnotations (Long projectId) {
        return ResponseEntity.ok(annotationRepository.findByProjectId(projectId));
    }

    public ResponseEntity<List<LabelEntity>> getAllLabels() {
        return ResponseEntity.ok(labelRepository.findAll());
    }
}
