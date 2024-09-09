package cz.cuni.mff.vopalenf.persistence.repositories;

import cz.cuni.mff.vopalenf.persistence.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}