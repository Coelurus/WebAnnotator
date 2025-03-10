package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.ai.GestureMagic;
import cz.cuni.mff.vopalenf.annotator.ai.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.request.LabelRequest;
import cz.cuni.mff.vopalenf.annotator.api.request.ProjectRequest;
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
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.manager.DataLoaderManager;
import cz.cuni.mff.vopalenf.annotator.manager.storage.StorageManager;
import cz.cuni.mff.vopalenf.annotator.mapper.AnnotationMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.LabelMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.ProjectMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
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

    private final DataLoaderManager dataLoaderManager;

    private final GestureMagic gestureMagic;

    private final LabelMapper labelMapper;

    private final AnnotationMapper annotationMapper;

    public ProjectService(ProjectRepository projectRepository,
                          TeamRepository teamRepository,
                          AnnotationRepository annotationRepository,
                          LabelRepository labelRepository,
                          StorageManager storageManager,
                          ProjectMapper projectMapper,
                          TeamMapper teamMapper,
                          DataLoaderManager dataLoaderManager,
                          GestureMagic gestureMagic,
                          LabelMapper labelMapper,
                          AnnotationMapper annotationMapper) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageManager = storageManager;
        this.annotationRepository = annotationRepository;
        this.labelRepository = labelRepository;
        this.projectMapper = projectMapper;
        this.teamMapper = teamMapper;
        this.dataLoaderManager = dataLoaderManager;
        this.gestureMagic = gestureMagic;
        this.labelMapper = labelMapper;
        this.annotationMapper = annotationMapper;
    }

    /**
     * Get list of all projects in system
     *
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectEntity -> projectMapper.mapProject(
                        projectEntity,
                        teamMapper.mapTeam(
                                projectEntity.getTeam(),
                                null
                        )
                ))
                .toList();
    }

    /**
     * Find project by its id
     *
     * @param projectId id of project to be found
     * @return Project response with corresponding id, throws exception otherwise
     * @throws NotFoundException Thrown when requested id is not owned by any project
     */
    public Project getProject(Long projectId) {
        ProjectEntity foundProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("PROJECT ID NOT FOUND", ProjectService.class.getSimpleName()));

        return projectMapper.mapProject(
                foundProject,
                teamMapper.mapTeam(
                        foundProject.getTeam()
                )
        );
    }

    /**
     * Get list of all priorities supported by server
     *
     * @return List of all available priorities
     */
    public List<Priority> getAllProjectPriorities() {
        return Arrays.stream(Priority.class.getEnumConstants()).toList();
    }

    /**
     * Toggle information about existence of annotation on frame in project
     *
     * @param projectId id of project where to add / remove annotation
     * @param frameId   id of a frame which to (un)annotate
     * @param labelId   ID of a label being used to annotate
     */
    public void annotateProjectFrame(Long projectId, Long frameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", ProjectService.class.getSimpleName());
        }
        //TODO: somehow get frame count and check whether frameId is valid
        annotateFrameInProject(projectId, frameId, labelId, false);
    }

    /**
     * Annotate all frames in range
     *
     * @param projectId    id of project to annotate frames in
     * @param startFrameId id of frame from which to annotate
     * @param endFrameId   id of frame to which annotate
     * @param labelId      id of a label to be used to annotate
     */
    public void annotateProjectFramesInRange(Long projectId, Long startFrameId, Long endFrameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", ProjectService.class.getSimpleName());
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            annotateFrameInProject(projectId, frameIdx, labelId, true);
        }
    }

    public void eraseAnnotationsInRange(Long projectId, Long startFrameId, Long endFrameId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("PROJECT ID NOT FOUND", ProjectService.class.getSimpleName());
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            eraseFrameAnnotation(projectId, frameIdx);
        }
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
    public List<Annotation> getAllAnnotations(Long projectId) {
        return annotationRepository.findByProjectId(projectId).stream()
                .map(annotationMapper::mapAnnotation)
                .toList();
    }

    /**
     * Get list of all existing labels in system
     *
     * @return List of all labels
     */
    public List<Label> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::mapLabel)
                .toList();
    }

    /**
     * Create new entity
     *
     * @param label Name and color of label to create
     * @return Newly created entity
     */
    public Label addLabel(LabelRequest label) {
        if (labelRepository.existsByLabel(label.getLabelName())) {
            throw new BadRequestException("Label already exists", ProjectService.class.getSimpleName());
        }
        if (!label.getColor().matches("^#([A-Fa-f0-9]{6})$")) {
            throw new BadRequestException(label.getColor() + " is not a valid color.", ProjectService.class.getSimpleName());
        }

        return labelMapper.mapLabel(labelRepository.save(
                LabelEntity.builder()
                        .label(label.getLabelName())
                        .color(label.getColor())
                        .build()
        ));
    }

    /**
     * Create new project and save its metadata into database and data into filesystem
     *
     * @param projectRequest Payload information about new project to create
     * @return Response status
     */
    public Project manageFileUpload(ProjectRequest projectRequest) {

        TeamEntity teamEntity = teamRepository
                .findById(projectRequest.getTeamId())
                .orElse(null);

        String compressedFileName = projectRequest.getFile().getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName)
                .substring(0, compressedFileName.indexOf(Constants.ARCHIVE_EXTENSION));

        ProjectEntity projectEntity = ProjectEntity.builder()
                .projectName(projectRequest.getProjectName())
                .logFileName(shortenedFileName)
                .deadline(projectRequest.getDeadline())
                .priority(projectRequest.getPriority())
                .team(teamEntity)
                .build();

        storageManager.store(projectRequest.getFile());
        return projectMapper.mapProject(projectRepository.save(projectEntity));
    }

    public List<PredictionTriple> trainAI(Long projectId) {
        Path pathToFS = Path.of(Constants.FILE_SYSTEM_PATH);
        Path projectPath = Arrays.stream(Objects.requireNonNull(pathToFS.toFile().listFiles()))
                .filter(file -> Objects.equals(projectRepository.findById(projectId)
                        .orElseThrow(() -> new NotFoundException("PROJECT_NOT_FOUND", ProjectService.class.getSimpleName()))
                        .getLogFileName(), file.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("PROJECT_NOT_FOUND", ProjectService.class.getSimpleName()))
                .toPath();

        projectPath = Path.of(projectPath.toString(), projectPath.getFileName().toString() + ".log");

        List<LogData> data = dataLoaderManager.loadLogFile(projectId, projectPath, false);

        gestureMagic.train(data, projectId);

        data = dataLoaderManager.loadLogFile(projectId, projectPath, true);

        return gestureMagic.test(data);
    }

    public void deleteProject(Long id) {
        String logFileName = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PROJECT_NOT_FOUND", ProjectEntity.class.getSimpleName()))
                .getLogFileName();
        storageManager.delete(logFileName);
        projectRepository.deleteById(id);
    }
}
