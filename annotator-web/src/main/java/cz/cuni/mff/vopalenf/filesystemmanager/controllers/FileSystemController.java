package cz.cuni.mff.vopalenf.filesystemmanager.controllers;

import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class FileSystemController {

    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public FileSystemController(TeamRepository teamRepository,
                                ProjectRepository projectRepository,
                                StorageService storageService) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.storageService = storageService;
    }

    @PostMapping("/uploadfile")
    public ModelAndView manageFileUpload(
            @RequestParam("project_name") String projectName,
            @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam("priority") Integer priority,
            @RequestParam("team_id") Integer teamId,
            @RequestParam("file") MultipartFile file) {

        Team team = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        Project project = new Project(projectName, file.getOriginalFilename(), deadline, priority, team);
        projectRepository.save(project);
        storageService.store(file);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        List<Team> teams = teamRepository.findAll();

        model.addAttribute("projects", projects);
        model.addAttribute("teams", teams);
        return "file-system";
    }

    @GetMapping("/projects/filter")
    public String filterProjects(
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Team team,
            Model model
    ) {
        List<Project> projects = projectRepository.findAll();
        System.out.println("HELP");
        // Apply filters
        if (priority != null) {
            projects = projects.stream()
                    .filter(project -> Objects.equals(project.getPriority(), priority))
                    .collect(Collectors.toList());
        }

        if (team != null) {
            projects = projects.stream()
                    .filter(project -> project.getTeam().equals(team))
                    .collect(Collectors.toList());
        }

        model.addAttribute("projects", projects);
        return "fragments/project-table :: project-table";
    }
}
