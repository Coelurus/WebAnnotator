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
 * Represents a label in the database.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "labels")
@Getter
@Setter
public class LabelEntity {
    /**
     * The unique identifier for the label.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * The label text.
     */
    @Column(name = "label")
    private String label;
    /**
     * The color associated with the label, used for visual representation.
     * Represented as a RGB hex color code (e.g., "#FF5733").
     */
    @Column(name = "color")
    private String color;
}
