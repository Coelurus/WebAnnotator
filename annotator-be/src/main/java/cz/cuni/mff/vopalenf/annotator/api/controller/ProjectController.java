package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.api.model.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Progress;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.request.LabelRequest;
import cz.cuni.mff.vopalenf.annotator.api.request.ProjectRequest;
import cz.cuni.mff.vopalenf.annotator.api.model.Priority;
import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Projects", description = "Endpoints for managing projects and related data")
@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(
            summary = "Get all existing projects",
            description = "Retrieves a list of all projects available in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "List of projects retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Project.class))
                    ),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(
            summary = "Get project by ID", description = "Retrieves a project based on the given ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the project", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @Operation(
            summary = "Delete project by ID", description = "Deletes a project based on the given ID.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the project", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update project by ID",
            description = "Updates a project based on the given ID and request body.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "projectId", description = "ID of the project", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Project request object containing necessary details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProjectRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    @PutMapping("/projects/{projectId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Project> updateProject(@RequestBody ProjectRequest project, @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.updateProject(projectId, project));
    }

    @Operation(
            summary = "Get all priorities",
            description = "Retrieves a list of all available priorities.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Priorities retrieved successfully")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/priorities")
    public ResponseEntity<List<Priority>> getAllProjectPriorities() {
        return ResponseEntity.ok(projectService.getAllProjectPriorities());
    }

    @Operation(
            summary = "Get all labels",
            description = "Retrieves a list of all labels.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Labels retrieved successfully")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/labels")
    public ResponseEntity<List<Label>> getAllLabels() {
        return ResponseEntity.ok(projectService.getAllLabels());
    }

    @Operation(
            summary = "Add a new label",
            description = "Creates a new label with the given details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Label request object containing necessary details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LabelRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Label created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/labels")
    public ResponseEntity<Label> addLabel(@RequestBody LabelRequest label) {
        return ResponseEntity.ok(projectService.addLabel(label));
    }

    @Operation(
            summary = "Upload project data",
            description = "Creates a new project, saves a file to the filesystem, and creates a new database record.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input format"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping(value = "/projects", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Project> manageFileUpload(
            @Parameter(description = "Name of the new project", required = true)
            @RequestPart("projectName") String projectName,
            @Parameter(description = "Deadline for project completion (yyyy-MM-dd format)", required = true)
            @RequestPart("deadline") String deadline,
            @Parameter(description = "Project priority (HIGH, MEDIUM, LOW)", required = true)
            @RequestPart("priority") String priority,
            @Parameter(description = "Team ID associated with the project", required = false)
            @RequestPart(value = "teamId", required = false) String teamId,
            @Parameter(description = "Compressed ZIP file containing log files and images", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        ProjectRequest projectRequest = ProjectRequest.builder()
                .projectName(projectName)
                .deadline(LocalDate.parse(deadline))
                .priority(priority)
                .teamId(teamId != null ? Long.valueOf(teamId) : null)
                .file(file)
                .build();
        Project newProject = projectService.manageFileUpload(projectRequest);
        return ResponseEntity.ok(newProject);
    }

    @Operation(
            summary = "Annotate a single frame",
            description = "Annotates a specific frame in a project with a given label.",
            parameters = {
                    @Parameter(name = "projectId", description = "Project ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "frameId", description = "Frame ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "labelId", description = "Label ID to use for annotation", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annotation added successfully"),
                    @ApiResponse(responseCode = "400", description = "Frame ID is out of range"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/projects/{projectId}/annotate/{frameId}/label/{labelId}")
    public ResponseEntity<Void> annotateProjectFrame(
            @PathVariable Long projectId,
            @PathVariable Long frameId,
            @PathVariable Long labelId) {
        projectService.annotateProjectFrame(projectId, frameId, labelId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Annotate a range of frames",
            description = "Annotates multiple frames in a project with a given label.",
            parameters = {
                    @Parameter(name = "projectId", description = "Project ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "startFrameId", description = "Starting frame ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "endFrameId", description = "Ending frame ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "labelId", description = "Label ID to use for annotation", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Frames annotated successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/projects/{projectId}/annotate/{startFrameId}/{endFrameId}/label/{labelId}")
    public ResponseEntity<Void> annotateProjectFramesInRange(
            @PathVariable Long projectId,
            @PathVariable Long startFrameId,
            @PathVariable Long endFrameId,
            @PathVariable Long labelId) {
        projectService.annotateProjectFramesInRange(projectId, startFrameId, endFrameId, labelId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Erase annotations in a range",
            description = "Removes annotations from a range of frames in a project.",
            parameters = {
                    @Parameter(name = "projectId", description = "Project ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "startFrameId", description = "Starting frame ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "endFrameId", description = "Ending frame ID", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annotations erased successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/projects/{projectId}/erase/{startFrameId}/{endFrameId}")
    public ResponseEntity<Void> eraseAnnotationsInRange(
            @PathVariable Long projectId,
            @PathVariable Long startFrameId,
            @PathVariable Long endFrameId
    ) {
        projectService.eraseAnnotationsInRange(projectId, startFrameId, endFrameId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all annotations for a project",
            description = "Retrieves a list of all annotations associated with a project.",
            parameters = {
                    @Parameter(name = "projectId", description = "Project ID", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of annotations retrieved successfully", content = @Content(schema = @Schema(implementation = Annotation.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(value = "/projects/{projectId}/annotations",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Annotation>> getAllAnnotations(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getAllAnnotations(projectId));
    }

    @Operation(
            summary = "Get all project progresses",
            description = "Retrieves a list of all progress statuses for projects.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of progresses retrieved successfully")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/projects/progresses")
    public ResponseEntity<List<Progress>> getAllProjectProgresses() {
        return ResponseEntity.ok(projectService.getAllProjectProgresses());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/projects/{projectId}/trainAI")
    public ResponseEntity<List<PredictionTriple>> trainAI(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.trainAI(projectId));
    }
}
