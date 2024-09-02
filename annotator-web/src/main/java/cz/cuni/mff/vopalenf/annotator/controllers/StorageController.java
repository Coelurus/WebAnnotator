package cz.cuni.mff.vopalenf.annotator.controllers;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;


public interface StorageController {
    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
