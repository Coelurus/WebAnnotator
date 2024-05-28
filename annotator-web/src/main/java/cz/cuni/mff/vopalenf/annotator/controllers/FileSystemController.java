package cz.cuni.mff.vopalenf.annotator.controllers;

import cz.cuni.mff.vopalenf.annotator.file_system.DataLoader;
import cz.cuni.mff.vopalenf.annotator.file_system.LogData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
public class FileSystemController {

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
}
