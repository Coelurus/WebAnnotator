package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert TeamEntity to Team and vice versa.
 */
@Component
public class TeamMapper {

    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;

    /**
     * Constructor for TeamMapper.
     *
     * @param modelMapper
     *            the ModelMapper instance to use for mapping
     * @param teamRepository
     *            the TeamRepository instance to use for fetching TeamEntity by ID
     */
    @Autowired
    public TeamMapper(ModelMapper modelMapper, TeamRepository teamRepository) {
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
    }

    /**
     * Maps a TeamEntity to a Team without associating it with a User.
     *
     * @param teamEntity
     *            the TeamEntity to map
     * @return the mapped Team, or null if the input is null
     */
    public Team mapTeam(TeamEntity teamEntity) {
        return mapTeam(teamEntity, null);
    }

    /**
     * Maps a TeamEntity to a Team, associating it with a User.
     *
     * @param teamEntity
     *            the TeamEntity to map
     * @param leader
     *            the User to associate with the Team
     * @return the mapped Team, or null if the input is null
     */
    public Team mapTeam(TeamEntity teamEntity, User leader) {
        if (teamEntity == null) {
            return null;
        }

        return Team.builder().id(teamEntity.getId()).name(teamEntity.getName()).leader(leader).build();
    }

    /**
     * Maps a Team to a TeamEntity.
     *
     * @param team
     *            the Team to map
     * @return the mapped TeamEntity, or null if the input is null
     */
    public TeamEntity mapTeamEntity(Team team) {
        if (team == null) {
            return null;
        }

        return modelMapper.map(team, TeamEntity.TeamEntityBuilder.class).build();
    }

    /**
     * Maps a team ID to a TeamEntity.
     *
     * @param teamId
     *            the ID of the team to map
     * @return the mapped TeamEntity, or null if the ID is null or not found
     */
    public TeamEntity mapTeamEntity(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId).orElse(null);
    }
}
