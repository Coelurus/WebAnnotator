package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.api.model.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Progress;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectExportAnnotated;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectExportWrapper;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String NO_GESTURE_LABEL = "NO_GESTURE";
    private static final List<String> COLUMNS_TO_IGNORE_CONTAINS = List.of("image");
    private static final String SEPARATOR_REGEX = "[,;]";
    private static final String SEPARATOR = ",";
    private static final String NEWLINE = "\n";
    private static final String LABEL_HEADER_NAME = "label";

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
        logger.info("Creating new label: {}", label.getLabelName());
        boolean existsByLabel = labelRepository.existsByLabel(label.getLabelName());
        if (existsByLabel) {
            logger.error("All of the labels already exists: {}", labelRepository.findAll());
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
    public ProjectExportAnnotated trainAI(Long projectId) {
        Project project = getProject(projectId);

        return aiClient.sendLogData(projectId, exportProjectDataAsCsv(projectId));
    }

    /**
     * Export project data as CSV content for download
     *
     * @param projectId
     *            ID of the project to export data for
     * @return CSV content as string
     */
    public ProjectExportWrapper exportProjectDataAsCsv(Long projectId) {
        Project project = getProject(projectId);
        String csvFilename = project.getLogFileName() + "\\" + project.getLogFileName() + ".csv";
        
        StringBuilder csvBuilder = new StringBuilder();

        try (InputStream inputStream = storageManager.loadAsStream(csvFilename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new ProjectExportWrapper (
                        project.getProjectName(),
                        csvBuilder.toString()
                ); // Return empty if no data
            }
            List<String> headers = Arrays.asList(headerLine.split(SEPARATOR_REGEX));
            List<Integer> columnsToIgnore = new ArrayList<>();
            Integer columnsCount = 0;
            for (int i = 0; i < headers.size(); i++) {
                int finalI = i;
                if (COLUMNS_TO_IGNORE_CONTAINS
                        .stream()
                        .anyMatch(keyword -> headers.get(finalI).toLowerCase().contains(keyword.toLowerCase()))
                ) {
                    columnsToIgnore.add(i);
                } else {
                    csvBuilder.append(headers.get(i)).append(SEPARATOR);
                    columnsCount++;
                }
            }
            csvBuilder.append(LABEL_HEADER_NAME).append(NEWLINE);
            columnsCount++; // For label column

            String line;
            Long frameIndex = 1L;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    frameIndex++;
                    continue;
                }
                
                String[] parts = line.split(SEPARATOR_REGEX);
                if (parts.length - columnsToIgnore.size() < columnsCount - 1) {
                    frameIndex++;
                    continue;
                }
                
                String label;
                AnnotationEntity annotationEntity = annotationRepository.findFirstByProjectIdAndFrameId(projectId, frameIndex);
                if (annotationEntity != null) {
                    label = labelRepository.findById(annotationEntity.getLabelId()).orElse(NO_GESTURE).getLabel();
                } else {
                    label = NO_GESTURE_LABEL;
                }

                String escapedLabel = escapeCSVField(label);
                for (int i = 0; i < columnsCount - 1; i++) {
                    if (columnsToIgnore.contains(i)) {
                        continue;
                    }
                    csvBuilder.append(parts[i]).append(SEPARATOR);
                }
                csvBuilder.append(escapedLabel).append(NEWLINE);
                
                frameIndex++;
            }
            
        } catch (IOException e) {
            throw new StorageException("Error reading CSV file for project " + projectId, e);
        }
        
        return new ProjectExportWrapper (
                project.getProjectName(),
                csvBuilder.toString()
        );
    }

    /**
     * Escape CSV field if it contains commas, quotes, or newlines
     *
     * @param field
     *            The field to escape
     * @return Escaped field
     */
    private String escapeCSVField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
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
