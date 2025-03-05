package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
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

    public void deleteTeam(Long teamId) {
        teamRepository.deleteById(teamId);
    }
}
