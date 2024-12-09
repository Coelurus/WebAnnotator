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
import cz.cuni.mff.vopalenf.annotator.util.ColorUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service taking care of projects
 */
@Service
public class ProjectService {

    private static final Long DEFAULT_ID = 0L;

    private final ProjectRepository projectRepository;

    private final TeamRepository teamRepository;

    private final AnnotationRepository annotationRepository;

    private final LabelRepository labelRepository;

    private final ProjectMapper projectMapper;

    private final TeamMapper teamMapper;

    private final StorageManager storageManager;

    private final ColorUtil colorUtil;

    private final FileSystemService fileSystemService;

    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          AnnotationRepository annotationRepository,
                          LabelRepository labelRepository,
                          StorageManager storageManager,
                          ProjectMapper projectMapper,
                          TeamMapper teamMapper,
                          FileSystemService fileSystemService,
                          ColorUtil colorUtil) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageManager = storageManager;
        this.annotationRepository = annotationRepository;
        this.labelRepository = labelRepository;
        this.projectMapper = projectMapper;
        this.teamMapper = teamMapper;
        this.fileSystemService = fileSystemService;
        this.colorUtil = colorUtil;
    }

    /**
     * Get list of all projects in system
     *
     * @return List of all projects
     */
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

    /**
     * Find project by its id
     *
     * @param projectId id of project to be found
     * @return Project response with corresponding id, throws exception otherwise
     * @throws NotFoundException Thrown when requested id is not owned by any project
     */
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

    /**
     * Get list of all priorities supported by server
     *
     * @return List of all available priorities
     */
    public ResponseEntity<List<Priority>> getAllProjectPriorities() {
        return ResponseEntity.ok(Arrays.stream(Priority.class.getEnumConstants()).toList());
    }

    /**
     * Toggle information about existence of annotation on frame in project
     *
     * @param projectId id of project where to add / remove annotation
     * @param frameId   id of a frame which to (un)annotate
     * @param labelId   ID of a label being used to annotate
     * @return Response status with information about success
     */
    public ResponseEntity<Void> annotateProjectFrame(Long projectId, Long frameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", "annotateProjectFrame");
        }
        //TODO: somehow get frame count and check whether frameId is valid
        annotateFrameInProject(projectId, frameId, labelId, false);
        return ResponseEntity.ok().build();
    }

    /**
     * Annotate all frames in range
     *
     * @param projectId    id of project to annotate frames in
     * @param startFrameId id of frame from which to annotate
     * @param endFrameId   id of frame to which annotate
     * @param labelId      id of a label to be used to annotate
     * @return Response status with success information
     */
    public ResponseEntity<Void> annotateProjectFramesInRange(Long projectId, Long startFrameId, Long endFrameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", "annotateProjectFrame");
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            annotateFrameInProject(projectId, frameIdx, labelId, true);
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> eraseAnnotationsInRange(Long projectId, Long startFrameId, Long endFrameId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", "annotateProjectFrame");
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            eraseFrameAnnotation(projectId, frameIdx);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Annotate frame in project
     *
     * @param projectId     id of project where to find frame to annotate
     * @param frameId       id of frame to annotate in project
     * @param labelId       id of a label to annotate frame with
     * @param forceAnnotate whether frame should be annotated even if it is already annotated
     */
    private void annotateFrameInProject(Long projectId, Long frameId, Long labelId, boolean forceAnnotate) {
        boolean annotationAlreadyExists
                = annotationRepository.existsByProjectIdAndFrameIdAndLabelId(projectId, frameId, DEFAULT_ID) && !forceAnnotate;
        if (annotationAlreadyExists) {
            annotationRepository.deleteByProjectIdAndFrameIdAndLabelId(projectId, frameId, DEFAULT_ID);
        } else {
            annotationRepository.save(
                    AnnotationEntity.builder()
                            .projectId(projectId)
                            .frameId(frameId)
                            .labelId(labelId)
                            .build()
            );
        }
    }

    /**
     * Erase all annotations of a frame in project
     *
     * @param projectId ID of project in which to erase annotations
     * @param frameId   ID of frame whose annotation to delete
     */
    private void eraseFrameAnnotation(Long projectId, Long frameId) {
        annotationRepository.deleteAllByProjectIdAndFrameId(projectId, frameId);
    }

    /**
     * Get all annotations used in a project
     *
     * @param projectId id of a project to identify team
     * @return List of all annotations on project defined by id
     */
    public ResponseEntity<List<AnnotationEntity>> getAllAnnotations(Long projectId) {
        return ResponseEntity.ok(annotationRepository.findByProjectId(projectId));
    }

    /**
     * Get list of all existing labels in system
     *
     * @return List of all labels
     */
    public ResponseEntity<List<LabelEntity>> getAllLabels() {
        return ResponseEntity.ok(labelRepository.findAll());
    }

    /**
     * Create new entity
     *
     * @param labelName Name of new label
     * @return Newly created entity
     */
    public ResponseEntity<LabelEntity> addLabel(String labelName) {
        if (labelRepository.existsByLabel(labelName)) {
            return ResponseEntity.badRequest().build();
        }
        LabelEntity newLabel = labelRepository.save(
                LabelEntity.builder()
                        .label(labelName)
                        .color(colorUtil.findLeastUsedColor())
                        .build()
        );
        return ResponseEntity.ok(newLabel);
    }

    /**
     * Create new project and save its metadata into database and data into filesystem
     *
     * @param projectName Name of the project
     * @param deadline    By when should be the project finished
     * @param priority    Importance of the project
     * @param teamId      ID of the team assigned to this project
     * @param file        Zipped file with data from sensor and camera
     * @return Response status
     */
    public ResponseEntity<String> manageFileUpload(String projectName, LocalDate deadline,
                                                   String priority, Integer teamId, MultipartFile file) {

        TeamEntity teamEntity = teamRepository.findById(Long.valueOf(teamId)).orElse(null);

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

        return ResponseEntity.ok().build();
    }
}
