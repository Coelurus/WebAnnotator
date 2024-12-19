package cz.cuni.mff.vopalenf.annotator.dao.model;


import cz.cuni.mff.vopalenf.annotator.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dao object representing user of the app
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    /**
     * ID of user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * First name of user
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Last name of user
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Username with which user will log into the system.
     */
    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Hashed password which will user use to log into the system
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Users role in system specifying accesses to different features
     */
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Team to which user belongs.
     */
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;
}
