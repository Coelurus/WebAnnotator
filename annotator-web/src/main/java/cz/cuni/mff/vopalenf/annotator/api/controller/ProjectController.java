package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.service.ProjectService;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import cz.cuni.mff.vopalenf.annotator.enums.ProjectPriority;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<ProjectPriority>> getAllProjectPriorities() {
        return projectService.getAllProjectPriorities();
    }

    @GetMapping("/labels")
    public ResponseEntity<List<LabelEntity>> getAllLabels() {
        return projectService.getAllLabels();
    }


    /**
     * Resolves creating new project and saving file to a filesystem and creating new record in db.
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

    @PostMapping("/projects/{projectId}/annotate/{frameId}")
    public ResponseEntity<Void> annotateProjectFrame( @PathVariable Long projectId, @PathVariable Long frameId) {
        return projectService.annotateProjectFrame(projectId, frameId);
    }

    @GetMapping(value = "/projects/{projectId}/annotations",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnotationEntity>> getAllAnnotations (@PathVariable Long projectId) {
        return projectService.getAllAnnotations(projectId);
    }

}
