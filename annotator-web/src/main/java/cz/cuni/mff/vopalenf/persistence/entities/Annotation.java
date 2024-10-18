package cz.cuni.mff.vopalenf.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "annotations")
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "frame_id")
    private Long frameId;

    @Column(name = "label")
    private String label;

    public Annotation() {

    }

    public Annotation(Long projectId, Long frameId, String label) {
        this.projectId = projectId;
        this.frameId = frameId;
        this.label = label;
    }
}
