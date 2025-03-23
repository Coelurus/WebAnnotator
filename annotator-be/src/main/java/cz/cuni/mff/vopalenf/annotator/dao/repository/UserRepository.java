package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Stores all users.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("update UserEntity u set u.team.id = :teamId where u.id = :id")
    @Transactional
    @Modifying
    void updateTeamIdById(Long teamId, Long id);
}
