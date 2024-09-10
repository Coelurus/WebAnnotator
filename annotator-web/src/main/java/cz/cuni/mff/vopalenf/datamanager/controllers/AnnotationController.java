package cz.cuni.mff.vopalenf.datamanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AnnotationController {

    @GetMapping("/annotator/{id}")
    public String annotationScreen(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "annotator";
    }
}
