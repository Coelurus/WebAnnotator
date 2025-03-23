package cz.cuni.mff.vopalenf.annotator.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @JsonCreator
    public static Role fromString(final String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
