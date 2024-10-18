package cz.cuni.mff.vopalenf.frontend.filesystem;

import cz.cuni.mff.vopalenf.constants.Constants;
import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.ProjectPriority;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.entities.User;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
public class UploadFileScreenController {

    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public UploadFileScreenController(TeamRepository teamRepository,
                                      ProjectRepository projectRepository,
                                      StorageService storageService,
                                      UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.storageService = storageService;
        this.userRepository = userRepository;
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

        return "index";
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
        model.addAttribute("priorities",
                List.of(ProjectPriority.LOW, ProjectPriority.MEDIUM, ProjectPriority.HIGH));
        return "project-form";
    }



}
