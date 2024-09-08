package cz.cuni.mff.vopalenf.persistence.repositories;

import cz.cuni.mff.vopalenf.persistence.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}