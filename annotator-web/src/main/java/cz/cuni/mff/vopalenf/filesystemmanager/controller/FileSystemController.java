package cz.cuni.mff.vopalenf.filesystemmanager.controller;

import cz.cuni.mff.vopalenf.filesystemmanager.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Takes care of working with log files and image files from camera.
 */
@RestController
public class FileSystemController {

    private final FileSystemService fileSystemService;

    @Autowired
    public FileSystemController(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }


    /**
     * Get a frame from concrete position from concrete project
     *
     * @param id       ID of project to load frame from
     * @param position Position of a frame from project
     * @return Image HTML tag with found frame
     */
    @GetMapping("/projects/{id}/frames/{position}")
    public String getFrame(@PathVariable Long id, @PathVariable int position) {
        return fileSystemService.getFrame(id, position);
    }
}
