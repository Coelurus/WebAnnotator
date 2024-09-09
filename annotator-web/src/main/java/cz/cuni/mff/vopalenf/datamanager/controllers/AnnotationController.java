package cz.cuni.mff.vopalenf.datamanager.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnotationController {

    @GetMapping("/annotation")
    public String annotationScreen() {
        return "annotator";
    }
}
