package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Stores all teams.
 */
@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    Optional<TeamEntity> findByName(String name);

    @Query("update TeamEntity t set t.leader.id = null where t.leader.id = :leaderId and t.id != :newTeamId")
    @Transactional
    @Modifying
    void deleteTeamLeaderFromOldTeam(Long leaderId, Long newTeamId);
}