package cz.cuni.mff.vopalenf.annotator.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * Enum representing user roles in the application.
 * This enum is used to define different roles that users can have, such as ADMIN and USER.
 */
@Getter
public enum Role {
    /**
     * Role representing an administrator with elevated privileges.
     */
    ROLE_ADMIN("ROLE_ADMIN"),
    /**
     * Role representing a regular user with standard privileges.
     */
    ROLE_USER("ROLE_USER");

    /**
     * The name of the role.
     */
    private final String name;

    /**
     * Constructor for the Role enum.
     *
     * @param name the name of the role
     */
    Role(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of the role.
     *
     * @param role the string representation of the role
     * @return the name of the role
     */
    @JsonCreator
    public static Role fromString(final String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
