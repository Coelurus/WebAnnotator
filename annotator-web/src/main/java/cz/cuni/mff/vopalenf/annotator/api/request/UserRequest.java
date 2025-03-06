package cz.cuni.mff.vopalenf.annotator.api.request;

import cz.cuni.mff.vopalenf.annotator.security.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private Long teamId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
