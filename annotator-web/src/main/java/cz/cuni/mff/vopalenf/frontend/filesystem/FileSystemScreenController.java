package cz.cuni.mff.vopalenf.frontend.filesystem;

import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.ProjectPriority;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class FileSystemScreenController {

    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public FileSystemScreenController(TeamRepository teamRepository,
                                      ProjectRepository projectRepository,
                                      StorageService storageService) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.storageService = storageService;
    }


    /**
     * Setting attributes so file system is generated properly
     *
     * @param model Holder for attributes
     * @return Redirection to a template HTML file
     */
    @GetMapping("/load-file-system")
    public String loadFileSystem(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("teams", teamRepository.findAll());
        model.addAttribute("priorities",
                List.of(ProjectPriority.LOW, ProjectPriority.MEDIUM, ProjectPriority.HIGH));
        return "file-system";
    }

    /**
     * Filter out projects from project board
     *
     * @param priority Level of priority for the projects to be shown
     * @param teamName Name of team for projects to be shown
     * @param model    Holder for attributes
     * @return Fragment of a page to swapped into currently shown file
     */
    @GetMapping("/filter")
    public String filterProjects(
            @RequestParam(name = "priority", required = false) Integer priority,
            @RequestParam(name = "teamName", required = false) String teamName,
            Model model
    ) {
        List<Project> projects = projectRepository.findAll();

        if (priority != null) {
            projects = projects.stream()
                    .filter(project -> Objects.equals(project.getPriority(), priority))
                    .toList();
        }

        Team team = teamRepository.findByName(teamName).orElse(null);

        if (team != null) {
            projects = projects.stream()
                    .filter(project -> project.getTeam().equals(team))
                    .toList();
        }

        model.addAttribute("projects", projects);
        return "fragments/project-table :: project-table";
    }
}
