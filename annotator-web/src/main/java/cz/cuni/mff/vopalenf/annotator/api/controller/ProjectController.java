package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.ai.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import cz.cuni.mff.vopalenf.annotator.enums.Priority;
import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return projectService.getAllProjects();
    }

    /**
     * Get project by ID
     *
     * @param id ID of project to get
     * @return Found project
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    /**
     * Get all existing priorities
     *
     * @return List of all priorities
     */
    @GetMapping("/priorities")
    public ResponseEntity<List<Priority>> getAllProjectPriorities() {
        return projectService.getAllProjectPriorities();
    }

    /**
     * Get all existing labels
     *
     * @return List of all labels
     */
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/labels")
    public ResponseEntity<List<LabelEntity>> getAllLabels() {
        return projectService.getAllLabels();
    }

    @PostMapping("/labels/{label_name}")
    public ResponseEntity<LabelEntity> addLabel(@PathVariable(name = "label_name") String labelName) {
        return projectService.addLabel(labelName);
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
    @PostMapping("/projects/upload")
    public ResponseEntity<String> manageFileUpload(
            @RequestParam("project_name") String projectName,
            @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam("priority") String priority,
            @RequestParam("team_id") Integer teamId,
            @RequestParam("file") MultipartFile file) {
        return projectService.manageFileUpload(projectName, deadline, priority, teamId, file);
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
        return projectService.annotateProjectFrame(projectId, frameId, labelId);
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
        return projectService.annotateProjectFramesInRange(projectId, startFrameId, endFrameId, labelId);
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
        return projectService.eraseAnnotationsInRange(projectId, startFrameId, endFrameId);
    }

    /**
     * Get list of all annotations on a project
     *
     * @param projectId ID of project from which to get all annotations
     * @return List of all annotations on one project
     */
    @GetMapping(value = "/projects/{projectId}/annotations",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnotationEntity>> getAllAnnotations(@PathVariable Long projectId) {
        return projectService.getAllAnnotations(projectId);
    }

    @PostMapping("/projects/{projectId}/trainAI")
    public ResponseEntity<List<PredictionTriple>> trainAI(@PathVariable Long projectId) {
        return projectService.trainAI(projectId);
    }

}
