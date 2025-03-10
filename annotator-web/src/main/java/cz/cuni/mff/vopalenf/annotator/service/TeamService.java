package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    private final UserMapper userMapper;

    private final TeamMapper teamMapper;

    @Autowired
    public TeamService(TeamRepository teamRepository,
                       UserMapper userMapper,
                       TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
    }

    /**
     * Get all teams in app
     *
     * @return List of all teams from database
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(teamEntity -> teamMapper.mapTeam(
                        teamEntity,
                        userMapper.mapUser(
                                teamEntity.getLeader()
                        )
                ))
                .toList();
    }

    /**
     * Delete team by its ID
     *
     * @param teamId ID of a team to delete
     * @throws NotFoundException when such team ID does not exist
     */
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new NotFoundException("Not found team with id " + teamId, TeamService.class.getSimpleName());
        }
        teamRepository.deleteById(teamId);
    }
}
