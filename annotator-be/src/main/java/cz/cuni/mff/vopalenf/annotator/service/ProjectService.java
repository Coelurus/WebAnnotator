package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.api.model.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Progress;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.request.LabelRequest;
import cz.cuni.mff.vopalenf.annotator.api.request.ProjectRequest;
import cz.cuni.mff.vopalenf.annotator.client.AIClient;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import cz.cuni.mff.vopalenf.annotator.api.model.Priority;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.AnnotationRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.LabelRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.enums.PriorityEnum;
import cz.cuni.mff.vopalenf.annotator.enums.ProgressEnum;
import cz.cuni.mff.vopalenf.annotator.exception.StorageException;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.storage.StorageManager;
import cz.cuni.mff.vopalenf.annotator.mapper.AnnotationMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.LabelMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.PriorityMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.ProgressMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.ProjectMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service taking care of projects
 */
@Service
public class ProjectService {

    /**
     * Path to the file system where files are stored. This value is read from
     * application properties.
     */
    @Value("${app.file-system.archive-extension}")
    String archiveExtension;

    private static final LabelEntity NO_GESTURE = LabelEntity.builder().label("NO_GESTURE").build();

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static final String PROJECT_NOT_FOUND_MSG = "PROJECT_NOT_FOUND";
    private static final Long DEFAULT_ID = 0L;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final AnnotationRepository annotationRepository;
    private final LabelRepository labelRepository;
    private final ProjectMapper projectMapper;
    private final TeamMapper teamMapper;
    private final ProgressMapper progressMapper;
    private final PriorityMapper priorityMapper;
    private final StorageManager storageManager;
    private final LabelMapper labelMapper;
    private final AnnotationMapper annotationMapper;
    private final FileSystemService fileSystemService;
    private final AIClient aiClient;

    /**
     * Constructor for ProjectService.
     *
     * @param projectRepository
     *            the ProjectRepository instance to use for fetching and saving
     *            projects
     * @param teamRepository
     *            the TeamRepository instance to use for fetching teams
     * @param annotationRepository
     *            the AnnotationRepository instance to use for managing annotations
     * @param labelRepository
     *            the LabelRepository instance to use for managing labels
     * @param storageManager
     *            the StorageManager instance to use for file storage operations
     * @param projectMapper
     *            the ProjectMapper instance to map between ProjectEntity and
     *            Project
     * @param teamMapper
     *            the TeamMapper instance to map between TeamEntity and Team
     * @param labelMapper
     *            the LabelMapper instance to map between LabelEntity and Label
     * @param annotationMapper
     *            the AnnotationMapper instance to map between AnnotationEntity and
     *            Annotation
     * @param fileSystemService
     *            the FileSystemService instance to manage file system operations
     * @param progressMapper
     *            the ProgressMapper instance to map between ProgressEnum and
     *            Progress
     * @param priorityMapper
     *            the PriorityMapper instance to map between PriorityEnum and
     *            Priority
     * @param aiClient
     *            the AIClient instance to interact with AI services
     */
    public ProjectService(ProjectRepository projectRepository, TeamRepository teamRepository,
            AnnotationRepository annotationRepository, LabelRepository labelRepository, StorageManager storageManager,
            ProjectMapper projectMapper, TeamMapper teamMapper, LabelMapper labelMapper,
            AnnotationMapper annotationMapper, FileSystemService fileSystemService, ProgressMapper progressMapper,
            PriorityMapper priorityMapper, AIClient aiClient) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.storageManager = storageManager;
        this.annotationRepository = annotationRepository;
        this.labelRepository = labelRepository;
        this.projectMapper = projectMapper;
        this.teamMapper = teamMapper;
        this.labelMapper = labelMapper;
        this.annotationMapper = annotationMapper;
        this.fileSystemService = fileSystemService;
        this.progressMapper = progressMapper;
        this.priorityMapper = priorityMapper;
        this.aiClient = aiClient;
    }

    /**
     * Get list of all projects in system
     *
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll().stream().map(projectEntity -> projectMapper.mapProject(projectEntity,
                teamMapper.mapTeam(projectEntity.getTeam(), null))).toList();
    }

    /**
     * Find project by its id
     *
     * @param projectId
     *            id of project to be found
     * @return Project response with corresponding id
     * @throws NotFoundException
     *             Thrown when requested id is not owned by any project
     */
    public Project getProject(Long projectId) {
        ProjectEntity foundProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Not found project with id" + projectId,
                        ProjectService.class.getSimpleName()));

        return projectMapper.mapProject(foundProject, teamMapper.mapTeam(foundProject.getTeam()));
    }

    /**
     * Get list of all priorities supported by server
     *
     * @return List of all available priorities
     */
    public List<Priority> getAllProjectPriorities() {
        return Arrays.stream(PriorityEnum.values()).map(priorityMapper::mapPriority).toList();
    }

    /**
     * Toggle information about existence of annotation on frame in project
     *
     * @param projectId
     *            id of project where to add / remove annotation
     * @param frameId
     *            id of a frame which to (un)annotate
     * @param labelId
     *            ID of a label being used to annotate
     * @throws NotFoundException
     *             when project ID does not exist
     * @throws BadRequestException
     *             when frame position is larger than frame count
     */
    public void annotateProjectFrame(Long projectId, Long frameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(PROJECT_NOT_FOUND_MSG, ProjectService.class.getSimpleName());
        }
        if (fileSystemService.getFramesCount(projectId).getCount() <= frameId) {
            throw new BadRequestException("Frame position is out of range", ProjectService.class.getSimpleName());
        }
        annotateFrameInProject(projectId, frameId, labelId, false);
    }

    /**
     * Annotate all frames in range
     *
     * @param projectId
     *            id of project to annotate frames in
     * @param startFrameId
     *            id of frame from which to annotate
     * @param endFrameId
     *            id of frame to which annotate
     * @param labelId
     *            id of a label to be used to annotate
     * @throws NotFoundException
     *             when project ID does not exist
     */
    public void annotateProjectFramesInRange(Long projectId, Long startFrameId, Long endFrameId, Long labelId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(PROJECT_NOT_FOUND_MSG, ProjectService.class.getSimpleName());
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            annotateFrameInProject(projectId, frameIdx, labelId, true);
        }
    }

    /**
     * Erase all annotations from frames in range
     *
     * @param projectId
     *            id of project to annotate frames in
     * @param startFrameId
     *            id of frame from which to annotate
     * @param endFrameId
     *            id of frame to which annotate
     * @throws NotFoundException
     *             when project ID does not exist
     */
    public void eraseAnnotationsInRange(Long projectId, Long startFrameId, Long endFrameId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(PROJECT_NOT_FOUND_MSG, ProjectService.class.getSimpleName());
        }
        for (Long frameIdx = startFrameId; frameIdx <= endFrameId; frameIdx++) {
            eraseFrameAnnotation(projectId, frameIdx);
        }
    }

    /**
     * Annotate frame in project
     *
     * @param projectId
     *            id of project where to find frame to annotate
     * @param frameId
     *            id of frame to annotate in project
     * @param labelId
     *            id of a label to annotate frame with
     * @param forceAnnotate
     *            whether frame should be annotated even if it is already annotated
     */
    private void annotateFrameInProject(Long projectId, Long frameId, Long labelId, boolean forceAnnotate) {
        boolean annotationAlreadyExists = annotationRepository.existsByProjectIdAndFrameIdAndLabelId(projectId, frameId,
                DEFAULT_ID) && !forceAnnotate;
        if (annotationAlreadyExists) {
            annotationRepository.deleteByProjectIdAndFrameIdAndLabelId(projectId, frameId, DEFAULT_ID);
        } else {
            annotationRepository
                    .save(AnnotationEntity.builder().projectId(projectId).frameId(frameId).labelId(labelId).build());
        }
    }

    /**
     * Erase all annotations of a frame in project
     *
     * @param projectId
     *            ID of project in which to erase annotations
     * @param frameId
     *            ID of frame whose annotation to delete
     */
    private void eraseFrameAnnotation(Long projectId, Long frameId) {
        annotationRepository.deleteAllByProjectIdAndFrameId(projectId, frameId);
    }

    /**
     * Get all annotations used in a project
     *
     * @param projectId
     *            id of a project to identify team
     * @return List of all annotations on project defined by id
     */
    public List<Annotation> getAllAnnotations(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(PROJECT_NOT_FOUND_MSG, ProjectService.class.getSimpleName());
        }
        return annotationRepository.findByProjectId(projectId).stream().map(annotationMapper::mapAnnotation).toList();
    }

    /**
     * Get list of all existing labels in system
     *
     * @return List of all labels
     */
    public List<Label> getAllLabels() {
        return labelRepository.findAll().stream().map(labelMapper::mapLabel).toList();
    }

    /**
     * Create new entity
     *
     * @param label
     *            Name and color of label to create
     * @return Newly created entity
     * @throws BadRequestException
     *             when label already exists or color is not valid
     */
    public Label addLabel(LabelRequest label) {
        boolean existsByLabel = labelRepository.existsByLabel(label.getLabelName());
        if (existsByLabel) {
            throw new BadRequestException("Label already exists", ProjectService.class.getSimpleName());
        }
        if (label.getColor() == null) {
            throw new BadRequestException("Color was not chosen.", ProjectService.class.getSimpleName());
        }
        if (!label.getColor().matches("^#([A-Fa-f0-9]{6})$")) {
            throw new BadRequestException(label.getColor() + " is not a valid color.",
                    ProjectService.class.getSimpleName());
        }
        return labelMapper.mapLabel(labelRepository
                .save(LabelEntity.builder().label(label.getLabelName()).color(label.getColor()).build()));
    }

    /**
     * Create new project and save its metadata into database and data into
     * filesystem
     *
     * @param projectRequest
     *            Payload information about new project to create
     * @return Response status
     * @throws StorageException
     *             when error occurs during file saving
     */
    public Project manageFileUpload(ProjectRequest projectRequest) {
        logger.info("Uploading file: {}", projectRequest.getFile().getOriginalFilename());

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TeamEntity teamEntity;
        if (Objects.equals(currentUser.getRole(), Role.ROLE_ADMIN.name())) {
            teamEntity = projectRequest.getTeamId() != null
                    ? teamRepository.findById(projectRequest.getTeamId()).orElse(null)
                    : null;
        } else {
            teamEntity = teamMapper.mapTeamEntity(currentUser.getTeam());
        }

        String compressedFileName = projectRequest.getFile().getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName).substring(0,
                compressedFileName.indexOf(archiveExtension));

        ProjectEntity projectEntity = ProjectEntity.builder().projectName(projectRequest.getProjectName())
                .logFileName(shortenedFileName).deadline(projectRequest.getDeadline())
                .priority(PriorityEnum.fromName(projectRequest.getPriority()))
                .progress(ProgressEnum.fromName(projectRequest.getProgress())).team(teamEntity).build();

        storageManager.store(projectRequest.getFile());

        logger.info("Stored file: {}", projectRequest.getFile().getOriginalFilename());
        return projectMapper.mapProject(projectRepository.save(projectEntity));
    }

    /**
     * Delete project by its ID
     *
     * @param id
     *            ID of a project to delete
     * @throws NotFoundException
     *             when project ID does not exist
     * @throws StorageException
     *             when folder does not exist or when deleting files fails
     */
    public void deleteProject(Long id) {
        logger.info("Deleting project: {}", id);
        String logFileName = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROJECT_NOT_FOUND_MSG, ProjectEntity.class.getSimpleName()))
                .getLogFileName();
        storageManager.delete(logFileName);
        projectRepository.deleteById(id);
    }

    /**
     * Get list of all progress states
     *
     * @return List of all progress states
     */
    public List<Progress> getAllProjectProgresses() {
        return Arrays.stream(ProgressEnum.values()).map(progressMapper::mapProgress).toList();
    }

    /**
     * Update project with new information
     *
     * @param projectId
     *            ID of project to update
     * @param project
     *            Project payload
     * @return Updated project
     */
    public Project updateProject(Long projectId, ProjectRequest project) {
        ProjectEntity projectToUpdate = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", ProjectService.class.getSimpleName()));

        projectToUpdate.setProjectName(project.getProjectName());
        projectToUpdate.setPriority(PriorityEnum.fromName(project.getPriority()));
        projectToUpdate.setDeadline(project.getDeadline());
        projectToUpdate.setProgress(ProgressEnum.fromName(project.getProgress()));
        projectToUpdate.setTeam(
                project.getTeamId() != null ? teamRepository.findById(project.getTeamId()).orElse(null) : null);

        return projectMapper.mapProject(projectRepository.save(projectToUpdate));
    }

    /**
     * Train AI model on project data
     *
     * @param projectId
     *            ID of the project to train AI on
     * @return List of prediction triples generated by AI
     */
    public List<PredictionTriple> trainAI(Long projectId) {
        Project project = getProject(projectId);
        byte[] logData = storageManager.load(project.getLogFileName() + "\\" + project.getLogFileName() + ".csv");
        String csv = new String(logData, StandardCharsets.UTF_8);

        List<LogData> logDataList = new ArrayList<>();
        String[] lines = csv.split("\n");

        for (Long i = 1L; i < lines.length; i++) {
            String line = lines[Math.toIntExact(i)].trim();
            String[] parts = line.split(",");

            if (parts.length < 5)
                continue;

            String timestampStr = parts[0];
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);
            double z = Double.parseDouble(parts[4]);

            double timeInSeconds = parseTimeToSeconds(timestampStr);

            String label;
            AnnotationEntity annotationEntity = annotationRepository.findByProjectIdAndFrameId(projectId, i - 1);
            if (annotationEntity != null) {
                label = labelRepository.findById(annotationEntity.getLabelId()).orElse(NO_GESTURE).getLabel();
            } else {
                label = "NO_GESTURE";
            }

            logDataList.add(LogData.builder().time(timeInSeconds).posX(x).posY(y).posZ(z).label(label).build());
        }

        return aiClient.sendLogData(projectId, logDataList);
    }

    /**
     * Parse timestamp string to seconds
     *
     * @param timestamp
     *            Timestamp string in format "yyyy-MM-dd HH:mm:ss"
     * @return Time in seconds since midnight
     */
    private double parseTimeToSeconds(String timestamp) {
        LocalTime time = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toLocalTime();
        return time.toSecondOfDay();
    }
}
