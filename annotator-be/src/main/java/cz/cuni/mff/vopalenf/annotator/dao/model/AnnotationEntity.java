package cz.cuni.mff.vopalenf.annotator.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an annotation in the database.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "annotations")
@Getter
@Setter
public class AnnotationEntity {
    /**
     * The unique identifier for the annotation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * The ID of the project the annotation belongs to.
     */
    @Column(name = "project_id")
    private Long projectId;
    /**
     * The ID of the frame in the project the annotation belongs to.
     */
    @Column(name = "frame_id")
    private Long frameId;
    /**
     * The ID of the label associated with the annotation.
     */
    @Column(name = "label_id")
    private Long labelId;
}
