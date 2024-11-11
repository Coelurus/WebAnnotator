package cz.cuni.mff.vopalenf.annotator.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dao object representing team of users inside the app
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teams")
@Getter
@Setter
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
}
