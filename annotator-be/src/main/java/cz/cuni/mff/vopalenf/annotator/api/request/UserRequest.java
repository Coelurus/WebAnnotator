package cz.cuni.mff.vopalenf.annotator.api.request;

import cz.cuni.mff.vopalenf.annotator.security.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a request to create or update a user.
 */
@Data
@Builder
public class UserRequest {
    /**
     * First name of the user.
     */
    private String firstName;
    /**
     * Last name of the user.
     */
    private String lastName;
    /**
     * Username of the user.
     */
    private String username;
    /**
     * ID of the team to which the user belongs.
     */
    private Long teamId;
    /**
     * Role of the user in the system.
     */
    @Enumerated(EnumType.STRING)
    private Role role;
}
