package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Stores all users.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
