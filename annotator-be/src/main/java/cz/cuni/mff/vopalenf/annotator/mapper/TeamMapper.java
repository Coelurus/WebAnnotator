package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;

    @Autowired
    public TeamMapper(ModelMapper modelMapper, TeamRepository teamRepository) {
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
    }

    public Team mapTeam(TeamEntity teamEntity) {
        return mapTeam(teamEntity, null);
    }

    public Team mapTeam(TeamEntity teamEntity, User leader) {
        if (teamEntity == null) {
            return null;
        }

        return Team.builder()
                .id(teamEntity.getId())
                .name(teamEntity.getName())
                .leader(leader)
                .build();
    }

    public TeamEntity mapTeamEntity(Team team) {
        if (team == null) {
            return null;
        }

        return modelMapper.map(team, TeamEntity.TeamEntityBuilder.class).build();
    }

    public TeamEntity mapTeamEntity(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId).orElse(null);
    }
}
