package cz.cuni.mff.vopalenf.persistence.repositories;

import cz.cuni.mff.vopalenf.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Stores all users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
