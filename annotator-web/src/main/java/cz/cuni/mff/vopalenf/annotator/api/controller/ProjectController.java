package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.ai.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.request.LabelRequest;
import cz.cuni.mff.vopalenf.annotator.api.request.ProjectRequest;
import cz.cuni.mff.vopalenf.annotator.enums.Priority;
import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Get all existing projects
     *
     * @return List of all projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /**
     * Get project by ID
     *
     * @param id ID of project to get
     * @return Found project
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    /**
     * Delete project by ID
     *
     * @param id ID of project to delete
     * @return Success on response on deletion
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all existing priorities
     *
     * @return List of all priorities
     */
    @GetMapping("/priorities")
    public ResponseEntity<List<Priority>> getAllProjectPriorities() {
        return ResponseEntity.ok(projectService.getAllProjectPriorities());
    }

    /**
     * Get all existing labels
     *
     * @return List of all labels
     */
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/labels")
    public ResponseEntity<List<Label>> getAllLabels() {
        return ResponseEntity.ok(projectService.getAllLabels());
    }

    @PostMapping("/labels")
    public ResponseEntity<Label> addLabel(@RequestBody LabelRequest label) {
        return ResponseEntity.ok(projectService.addLabel(label));
    }

    /**
     * Controls creating new project and saving file to a filesystem and creating new record in db.
     *
     * @param projectName Name of the new project
     * @param deadline    Date till which the project should be finished.
     * @param priority    A need to finish this project.
     * @param teamId      Identifier of team to which the project is assigned to.
     * @param file        Compressed zip file containing log file and camera shots.
     * @return Redirection to a main menu.
     */
    @PostMapping(value = "/projects", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Project> manageFileUpload(
            @RequestPart("projectName") String projectName,
            @RequestPart("deadline") String deadline,
            @RequestPart("priority") String priority,
            @RequestPart("teamId") String teamId,
            @RequestPart("file") MultipartFile file
    ) {
        ProjectRequest projectRequest = ProjectRequest.builder()
                .projectName(projectName)
                .deadline(LocalDate.parse(deadline))
                .priority(Priority.valueOf(priority))
                .teamId(Long.valueOf(teamId))
                .file(file)
                .build();
        Project newProject = projectService.manageFileUpload(projectRequest);
        return ResponseEntity.ok(newProject);
    }

    /**
     * Controls annotating frame in project with label
     *
     * @param projectId ID of project to annotate
     * @param frameId   ID of a frame to annotate
     * @param labelId   ID of a label being used to annotate
     * @return Response status about success of request
     */
    @PostMapping("/projects/{projectId}/annotate/{frameId}/label/{labelId}")
    public ResponseEntity<Void> annotateProjectFrame(
            @PathVariable Long projectId,
            @PathVariable Long frameId,
            @PathVariable Long labelId) {
        projectService.annotateProjectFrame(projectId, frameId, labelId);
        return ResponseEntity.ok().build();
    }

    /**
     * Controls annotating range of frames in project with label
     *
     * @param projectId    ID of project to annotate
     * @param startFrameId ID of a first frame to annotate
     * @param endFrameId   ID of a last frame to annotate
     * @param labelId      ID of a label being used to annotate
     * @return Response status about success of request
     */
    @PostMapping("/projects/{projectId}/annotate/{startFrameId}/{endFrameId}/label/{labelId}")
    public ResponseEntity<Void> annotateProjectFramesInRange(
            @PathVariable Long projectId,
            @PathVariable Long startFrameId,
            @PathVariable Long endFrameId,
            @PathVariable Long labelId) {
        projectService.annotateProjectFramesInRange(projectId, startFrameId, endFrameId, labelId);
        return ResponseEntity.ok().build();
    }

    /**
     * Controls removing annotations from frames in project
     *
     * @param projectId    ID of project from where to remove annotations
     * @param startFrameId From which frame to remove annotations
     * @param endFrameId   To which frame to remove annotations
     * @return Response status about success of request
     */
    @PostMapping("/projects/{projectId}/erase/{startFrameId}/{endFrameId}")
    public ResponseEntity<Void> eraseAnnotationsInRange(
            @PathVariable Long projectId,
            @PathVariable Long startFrameId,
            @PathVariable Long endFrameId
    ) {
        projectService.eraseAnnotationsInRange(projectId, startFrameId, endFrameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get list of all annotations on a project
     *
     * @param projectId ID of project from which to get all annotations
     * @return List of all annotations on one project
     */
    @GetMapping(value = "/projects/{projectId}/annotations",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Annotation>> getAllAnnotations(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getAllAnnotations(projectId));
    }

    @PostMapping("/projects/{projectId}/trainAI")
    public ResponseEntity<List<PredictionTriple>> trainAI(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.trainAI(projectId));
    }

}
