package cz.cuni.mff.vopalenf.annotator.api.request;

import cz.cuni.mff.vopalenf.annotator.security.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRequest {

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    String username;

    @NotBlank
    String password;

    @Enumerated(EnumType.STRING)
    Role role;
}
