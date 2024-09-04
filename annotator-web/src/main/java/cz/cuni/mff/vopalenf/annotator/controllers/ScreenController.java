package cz.cuni.mff.vopalenf.annotator.controllers;

import cz.cuni.mff.vopalenf.annotator.storage.entities.Team;
import cz.cuni.mff.vopalenf.annotator.storage.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class ScreenController {

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/")
    public String showUploadForm(Model model) {
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("teams", teams);
        return "upload";
    }
}