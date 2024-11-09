package cz.cuni.mff.vopalenf.annotator.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.model.TeamResponse;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import cz.cuni.mff.vopalenf.annotator.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeamApiController {

    private final TeamService teamService;

    public TeamApiController(TeamService teamService) {
        this.teamService = teamService;
    }

    @JsonView({Views.ShowUsersInTeams.class})
    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return teamService.getAllTeams();
    }
}
