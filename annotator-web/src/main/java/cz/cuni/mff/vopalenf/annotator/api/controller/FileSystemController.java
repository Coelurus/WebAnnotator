package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * Takes care of working with log files and image files from camera.
 */
@RestController
@RequestMapping("/api")
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
    @GetMapping(
            value = "/projects/{id}/frame/{position}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<Resource> getFrame(@PathVariable Long id, @PathVariable Integer position) {
        return fileSystemService.getFrame(id, position);
    }

    @GetMapping(value = "/projects/{id}/frame/count",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> getFramesCount(@PathVariable Long id) {
        return fileSystemService.getFramesCount(id);
    }
}
