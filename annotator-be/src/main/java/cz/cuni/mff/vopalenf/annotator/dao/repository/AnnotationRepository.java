package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<AnnotationEntity, Long> {
    List<AnnotationEntity> findByProjectId(Long projectId);

    Boolean existsByProjectIdAndFrameIdAndLabelId(Long projectId, Long frameId, Long labelId);

    Boolean existsByProjectIdAndFrameId(Long projectId, Long frameId);

    AnnotationEntity findFirstByProjectIdAndFrameId(Long projectId, Long frameId);

    @Transactional
    void deleteByProjectIdAndFrameIdAndLabelId(Long projectId, Long frameId, Long labelId);

    @Transactional
    void deleteAllByProjectIdAndFrameId(Long projectId, Long frameId);
}
