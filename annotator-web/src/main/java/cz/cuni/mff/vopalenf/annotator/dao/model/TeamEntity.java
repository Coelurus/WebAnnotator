package cz.cuni.mff.vopalenf.annotator.dao.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teams")
public class TeamEntity {

    /**
     * Identifier of a team
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Name of a team
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Leader of a team
     */
    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    private UserEntity leader;

    public TeamEntity() {
    }

    public TeamEntity(String name) {
        this.name = name;
    }
}
