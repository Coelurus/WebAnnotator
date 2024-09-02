package cz.cuni.mff.vopalenf.annotator.controllers;

import cz.cuni.mff.vopalenf.annotator.file_system.DataLoader;
import cz.cuni.mff.vopalenf.annotator.file_system.LogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@RestController
public class FileSystemController {

    private final StorageController storageController;

    @Autowired
    public FileSystemController(StorageController storageController) {
        this.storageController = storageController;
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
    public String manageFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        storageController.store(file);
        System.out.println("WUT?");
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }
}
