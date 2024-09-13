package cz.cuni.mff.vopalenf.frontend;

import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.entities.User;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controls proper generation of frontend HTML.
 */
@Controller
public class ScreenController {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ScreenController(TeamRepository teamRepository,
                            UserRepository userRepository,
                            ProjectRepository projectRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Setting attributes so main page is generated properly
     *
     * @param model Holder for attributes
     * @return Redirection to a template HTML file
     */
    @GetMapping("/")
    public String showUploadForm(Model model) {
        List<Team> teams = teamRepository.findAll();
        List<Project> projects = projectRepository.findAll();
        List<User> users = userRepository.findAll();

        model.addAttribute("teams", teams);
        model.addAttribute("projects", projects);
        model.addAttribute("users", users);

        return "upload";
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
        return "file-system";
    }

    /**
     * Setting attributes so form for creating project is generated properly
     *
     * @param model Holder for attributes
     * @return Redirection to a template HTML file
     */
    @GetMapping("/project-form")
    public String getProjectForm(Model model) {
        model.addAttribute("teams", teamRepository.findAll());
        return "project-form";
    }


    /**
     * Setting attributes so file system frontend is generated properly
     *
     * @param model Holder for attributes
     * @return Redirection to a template HTML file
     */
    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        List<Team> teams = teamRepository.findAll();

        model.addAttribute("projects", projects);
        model.addAttribute("teams", teams);
        return "file-system";
    }

}