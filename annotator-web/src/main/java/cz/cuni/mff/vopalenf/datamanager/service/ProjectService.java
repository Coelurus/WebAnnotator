package cz.cuni.mff.vopalenf.datamanager.service;

import cz.cuni.mff.vopalenf.persistence.entities.ProjectPriority;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.response.ProjectResponse;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll().stream()
                .map(project -> new ProjectResponse(project, Views.ShowTeamsInUsers.class))
                .toList()
        );
    }

    public ResponseEntity<List<ProjectPriority>> getAllProjectPriorities() {
        return ResponseEntity.ok(Arrays.stream(ProjectPriority.class.getEnumConstants()).toList());
    }
}
