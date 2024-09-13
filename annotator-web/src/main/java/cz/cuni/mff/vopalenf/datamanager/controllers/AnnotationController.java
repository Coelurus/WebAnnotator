package cz.cuni.mff.vopalenf.datamanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for proper functioning of annotator function.
 */
@Controller
@RequestMapping("/annotator")
public class AnnotationController {

    /**
     * Takes care of setting parameters for properly generating annotation screen.
     *
     * @param id    ID of project to be annotated
     * @param model Holder for attributes
     * @return Redirection to template
     */
    @GetMapping("/{id}")
    public String annotationScreen(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("imagesOnPage", 6);
        return "annotator";
    }
}
