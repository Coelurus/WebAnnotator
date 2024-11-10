package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public TeamMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
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
}
