package cz.cuni.mff.vopalenf.frontend;

import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.entities.User;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

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

    @GetMapping("/load-file-system")
    public String loadFileSystem(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("teams", teamRepository.findAll());
        return "file-system";
    }
}