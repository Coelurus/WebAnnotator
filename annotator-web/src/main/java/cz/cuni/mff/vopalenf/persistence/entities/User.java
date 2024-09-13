package cz.cuni.mff.vopalenf.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

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
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    /**
     * Hashed password which will user use to log into the system
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Team to which user belongs.
     */
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
