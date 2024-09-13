package cz.cuni.mff.vopalenf.datamanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/annotator")
public class AnnotationController {

    @GetMapping("/{id}")
    public String annotationScreen(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("imagesOnPage", 6);
        return "annotator";
    }
}
