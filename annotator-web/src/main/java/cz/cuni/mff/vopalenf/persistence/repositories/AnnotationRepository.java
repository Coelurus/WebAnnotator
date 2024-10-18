package cz.cuni.mff.vopalenf.persistence.repositories;

import cz.cuni.mff.vopalenf.persistence.entities.Annotation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    List<Annotation> findByProjectId(Long projectId);
    Boolean existsByProjectIdAndFrameIdAndLabel(Long projectId, Long frameId, String label);
    @Transactional
    Integer deleteByProjectIdAndFrameIdAndLabel(Long projectId, Long frameId, String label);

}
