package cz.cuni.mff.vopalenf.filesystemmanager.controllers;

import cz.cuni.mff.vopalenf.datamanager.DataLoader;
import cz.cuni.mff.vopalenf.datamanager.LogData;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Path;
import java.time.LocalDate;

@RestController
public class FileSystemController {

    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public FileSystemController(TeamRepository teamRepository,
                                UserRepository userRepository,
                                ProjectRepository projectRepository,
                                StorageService storageService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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
}
