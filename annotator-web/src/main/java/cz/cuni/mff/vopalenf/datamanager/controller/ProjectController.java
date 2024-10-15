package cz.cuni.mff.vopalenf.datamanager.controller;

import cz.cuni.mff.vopalenf.datamanager.service.ProjectService;
import cz.cuni.mff.vopalenf.persistence.entities.ProjectPriority;
import cz.cuni.mff.vopalenf.persistence.response.ProjectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/priorities")
    public ResponseEntity<List<ProjectPriority>> getAllProjectPriorities() {
        return projectService.getAllProjectPriorities();
    }

}
