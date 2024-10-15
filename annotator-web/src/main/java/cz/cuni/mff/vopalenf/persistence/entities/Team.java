package cz.cuni.mff.vopalenf.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teams")
public class Team {

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
    private User leader;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }
}
