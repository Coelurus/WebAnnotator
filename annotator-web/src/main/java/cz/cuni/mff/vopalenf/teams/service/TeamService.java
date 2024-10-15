package cz.cuni.mff.vopalenf.teams.service;

import cz.cuni.mff.vopalenf.persistence.repositories.TeamRepository;
import cz.cuni.mff.vopalenf.persistence.response.TeamResponse;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(
                teamRepository.findAll().stream()
                        .map(team -> new TeamResponse(team, Views.ShowUsersInTeams.class))
                        .toList()
        );
    }

}
