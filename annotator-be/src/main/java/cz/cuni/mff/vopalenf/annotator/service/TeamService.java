package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.api.request.TeamRequest;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.TeamRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.mapper.TeamMapper;
import cz.cuni.mff.vopalenf.annotator.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing teams in the application. Provides methods to create,
 * update, delete, and retrieve teams.
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TeamMapper teamMapper;

    /**
     * Constructor for TeamService.
     *
     * @param teamRepository
     *            TeamRepository instance to use for database operations
     * @param userRepository
     *            UserRepository instance to use for user operations
     * @param userMapper
     *            UserMapper instance to map UserEntity to User
     * @param teamMapper
     *            TeamMapper instance to map TeamEntity to Team
     */
    @Autowired
    public TeamService(TeamRepository teamRepository, UserRepository userRepository, UserMapper userMapper,
            TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
        this.userRepository = userRepository;
    }

    /**
     * Get all teams in app
     *
     * @return List of all teams from database
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(teamEntity -> teamMapper.mapTeam(teamEntity, userMapper.mapUser(teamEntity.getLeader()))).toList();
    }

    /**
     * Delete team by its ID
     *
     * @param teamId
     *            ID of a team to delete
     * @throws NotFoundException
     *             when such team ID does not exist
     */
    public void deleteTeam(Long teamId) {
        logger.info("Deleting team with id: {}", teamId);

        if (!teamRepository.existsById(teamId)) {
            logger.error("Team with id {} does not exist", teamId);
            throw new NotFoundException("Not found team with id " + teamId, TeamService.class.getSimpleName());
        }
        teamRepository.deleteById(teamId);

        logger.info("Deleted team with id: {}", teamId);
    }

    /**
     * Create new team and save it to the database. If leaderId is not null, make
     * the leader member of the new team and remove his leadership from other teams.
     *
     * @param teamRequest
     *            Team payload
     * @return newly created team
     */
    public Team createTeam(TeamRequest teamRequest) {
        logger.info("Creating new team with name: {}", teamRequest.getName());

        TeamEntity teamEntity = TeamEntity.builder().name(teamRequest.getName())
                .leader(userMapper.mapUserEntity(teamRequest.getLeaderId())).build();
        TeamEntity savedTeamEntity = teamRepository.save(teamEntity);

        if (teamRequest.getLeaderId() != null) {
            teamRepository.deleteTeamLeaderFromOldTeam(teamRequest.getLeaderId(), savedTeamEntity.getId());
            userRepository.updateTeamIdById(savedTeamEntity.getId(), teamRequest.getLeaderId());
        }

        logger.info("Created new team named {}", savedTeamEntity.getName());
        return teamMapper.mapTeam(savedTeamEntity);
    }

    /**
     * Update existing team in database
     *
     * @param teamId
     *            ID of a team to update
     * @param team
     *            Team payload
     * @return updated team
     */
    public Team updateTeam(Long teamId, TeamRequest team) {
        logger.info("Updating team with id: {}", teamId);

        TeamEntity teamToUpdate = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found", TeamService.class.getSimpleName()));

        teamToUpdate.setName(team.getName());
        teamToUpdate.setLeader(userMapper.mapUserEntity(team.getLeaderId()));

        logger.info("Updated team with id: {}", teamId);
        return teamMapper.mapTeam(teamRepository.save(teamToUpdate));
    }

    /**
     * Add a member to a team
     *
     * @param teamId
     *            ID of the team
     * @param userId
     *            ID of the user to add as a member
     * @throws NotFoundException
     *             when the team ID or user ID does not exist
     */
    public void addTeamMember(Long teamId, Long userId) {
        logger.info("Adding user with id {} into team with id {}", userId, teamId);

        TeamEntity teamEntity = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found", TeamService.class.getSimpleName()));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found", TeamService.class.getSimpleName()));
        userEntity.setTeam(teamEntity);
        userRepository.save(userEntity);

        logger.info("Added user {} to team named {}", userEntity.getUsername(), teamEntity.getName());
    }
}
