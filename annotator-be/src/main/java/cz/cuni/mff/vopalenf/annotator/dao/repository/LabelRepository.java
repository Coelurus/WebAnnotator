package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<LabelEntity, Long> {
    Boolean existsByLabel(String label);
}
