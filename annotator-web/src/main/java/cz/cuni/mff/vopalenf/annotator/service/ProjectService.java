package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.constants.Constants;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.AnnotationRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.LabelRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.enums.Priority;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.manager.storage.StorageManager;
import cz.cuni.mff.vopalenf.annotator.mapper.ProjectMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    private static final String DEFAULT = "default";

    private final ProjectRepository projectRepository;

    private final TeamRepository teamRepository;

    private final AnnotationRepository annotationRepository;

    private final LabelRepository labelRepository;

    private final ProjectMapper projectMapper;

    private final TeamMapper teamMapper;

    private final StorageManager storageManager;

    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          AnnotationRepository annotationRepository,
                          LabelRepository labelRepository,
                          StorageManager storageManager,
                          ProjectMapper projectMapper,
                          TeamMapper teamMapper) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageManager = storageManager;
        this.annotationRepository = annotationRepository;
        this.labelRepository = labelRepository;
        this.projectMapper = projectMapper;
        this.teamMapper = teamMapper;
    }

    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll().stream()
                .map(projectEntity -> projectMapper.mapProject(
                        projectEntity,
                        teamMapper.mapTeam(
                                projectEntity.getTeam(),
                                null
                        )
                ))
                .toList()
        );
    }

    public ResponseEntity<Project> getProject(Long projectId) {
        ProjectEntity foundProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("PROJECT ID NOT FOUND", "getProject"));

        return ResponseEntity.ok(projectMapper.mapProject(
                foundProject,
                teamMapper.mapTeam(
                        foundProject.getTeam()
                )
        ));
    }

    public ResponseEntity<List<Priority>> getAllProjectPriorities() {
        return ResponseEntity.ok(Arrays.stream(Priority.class.getEnumConstants()).toList());
    }

    public ResponseEntity<String> manageFileUpload(String projectName, LocalDate deadline,
                                                   String priority, Integer teamId, MultipartFile file) {

        TeamEntity teamEntity = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        String compressedFileName = file.getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName)
                .substring(0, compressedFileName.indexOf(Constants.ARCHIVE_EXTENSION));

        ProjectEntity projectEntity = ProjectEntity.builder()
                .projectName(projectName)
                .logFileName(shortenedFileName)
                .deadline(deadline)
                .priority(Priority.fromName(priority))
                .team(teamEntity)
                .build();
        projectRepository.save(projectEntity);
        storageManager.store(file);

        return ResponseEntity.ok("OK");
    }

    public ResponseEntity<Void> annotateProjectFrame(Long projectId, Long frameId) {
        if (annotationRepository.existsByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT)) {
            annotationRepository.deleteByProjectIdAndFrameIdAndLabel(projectId, frameId, DEFAULT);
        } else {
            annotationRepository.save(
                    AnnotationEntity.builder()
                            .projectId(projectId)
                            .frameId(frameId)
                            .label(DEFAULT)
                            .build()
            );
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<AnnotationEntity>> getAllAnnotations(Long projectId) {
        return ResponseEntity.ok(annotationRepository.findByProjectId(projectId));
    }

    public ResponseEntity<List<LabelEntity>> getAllLabels() {
        return ResponseEntity.ok(labelRepository.findAll());
    }
}
