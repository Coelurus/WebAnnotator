package cz.cuni.mff.vopalenf.annotator.controllers;

import cz.cuni.mff.vopalenf.annotator.file_system.DataLoader;
import cz.cuni.mff.vopalenf.annotator.file_system.LogData;
import cz.cuni.mff.vopalenf.annotator.storage.entities.Project;
import cz.cuni.mff.vopalenf.annotator.storage.StorageService;
import cz.cuni.mff.vopalenf.annotator.storage.entities.Team;
import cz.cuni.mff.vopalenf.annotator.storage.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.annotator.storage.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.storage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@RestController
public class FileSystemController {

    private final StorageService storageService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    public FileSystemController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files")
    public Path[] files() {
        DataLoader dataLoader = new DataLoader();
        return dataLoader.loadFromResources();
    }

    @GetMapping("/file")
    public LogData[] file() {
        DataLoader dataLoader = new DataLoader();
        return dataLoader.loadLogFile(dataLoader.loadFromResources()[0]);
    }

    @PostMapping("/uploadfile")
    public ModelAndView manageFileUpload(
            @RequestParam("project_name") String projectName,
            @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam("priority") Integer priority,
            @RequestParam("team_id") Integer teamId,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Team team = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        Project project = new Project(projectName, file.getName(), deadline, priority, team);
        projectRepository.save(project);
        storageService.store(file);
        System.out.println("WUT?");

        return new ModelAndView("redirect:/");
    }
}
