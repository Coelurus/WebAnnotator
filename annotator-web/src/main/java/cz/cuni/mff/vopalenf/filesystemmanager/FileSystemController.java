package cz.cuni.mff.vopalenf.filesystemmanager;

import cz.cuni.mff.vopalenf.constants.Constants;
import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageService;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import cz.cuni.mff.vopalenf.persistence.repositories.ProjectRepository;
import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Takes care of working with log files and image files from camera.
 */
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
    @PostMapping("/uploadfile")
    public String manageFileUpload(
            @RequestParam("project_name") String projectName,
            @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam("priority") Integer priority,
            @RequestParam("team_id") Integer teamId,
            @RequestParam("file") MultipartFile file) {

        Team team = teamRepository.findById(Long.valueOf(teamId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));

        String compressedFileName = file.getOriginalFilename();
        String shortenedFileName = Objects.requireNonNull(compressedFileName)
                .substring(0, compressedFileName.indexOf(Constants.ARCHIVE_EXTENSION));
        Project project = new Project(projectName, shortenedFileName, deadline, priority, team);
        projectRepository.save(project);
        storageService.store(file);
        return "redirect:/";
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
                    .collect(Collectors.toList());
        }

        Team team = teamRepository.findByName(teamName).orElse(null);

        if (team != null) {
            projects = projects.stream()
                    .filter(project -> project.getTeam().equals(team))
                    .collect(Collectors.toList());
        }

        model.addAttribute("projects", projects);
        return "fragments/project-table :: project-table";
    }

    /**
     * Get a frame from concrete position from concrete project
     *
     * @param id       ID of project to load frame from
     * @param position Position of a frame from project
     * @return Image HTML tag with found frame
     */
    @GetMapping("/projects/{id}/frames/{position}")
    @ResponseBody
    public String getFrame(@PathVariable Long id, @PathVariable int position) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));
        String logFileName = project.getLogFileName();
        Path pathToFS = Path.of(Constants.FILE_SYSTEM_PATH);

        File projectDir = Arrays.stream(Objects.requireNonNull(pathToFS.toFile().listFiles()))
                .filter(file -> file.getName().equals(logFileName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invalid project id"));


        File[] imageFiles = Objects.requireNonNull(projectDir.listFiles(((dir, name) -> name.toLowerCase().endsWith(".jpg"))));
        Arrays.sort(imageFiles);
        Arrays.sort(imageFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getPath().substring(f.getPath().indexOf("frame_") + 6, f.getPath().indexOf("_msec.jpg")))));
        return "<img src='\\" + imageFiles[position].getPath() + "' alt='annotation preview' width='200px' height='200px'/>";
    }
}
