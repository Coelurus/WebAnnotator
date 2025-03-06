package cz.cuni.mff.vopalenf.annotator.security;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_ADMIN,
    ROLE_USER;

    @JsonCreator
    public static Role fromString(final String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
