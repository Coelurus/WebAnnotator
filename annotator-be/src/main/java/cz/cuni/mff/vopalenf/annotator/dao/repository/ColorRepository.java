package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<ColorEntity, Long> {
}
