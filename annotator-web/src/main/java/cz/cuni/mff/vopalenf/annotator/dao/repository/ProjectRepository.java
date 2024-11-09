package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Stores all existing projects
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}