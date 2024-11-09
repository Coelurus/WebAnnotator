package cz.cuni.mff.vopalenf.persistence.repositories;

import cz.cuni.mff.vopalenf.persistence.entities.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
}
