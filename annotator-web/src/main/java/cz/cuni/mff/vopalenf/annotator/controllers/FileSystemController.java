package cz.cuni.mff.vopalenf.annotator.controllers;

import cz.cuni.mff.vopalenf.annotator.file_system.DataLoader;
import cz.cuni.mff.vopalenf.annotator.file_system.LogData;
import cz.cuni.mff.vopalenf.annotator.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@RestController
public class FileSystemController {

    private final StorageService storageService;

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
    public ModelAndView manageFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        storageService.store(file);
        System.out.println("WUT?");
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return new ModelAndView("redirect:/");
    }
}
