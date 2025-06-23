package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the annotation system.
 */
@Builder(toBuilder = true)
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    /**
     * The ID of the user.
     */
    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    Long id;

    /**
     * The first name of the user.
     */
    @JsonProperty("firstName")
    @JsonView(Views.BothView.class)
    String firstName;

    /**
     * The last name of the user.
     */
    @JsonProperty("lastName")
    @JsonView(Views.BothView.class)
    String lastName;

    /**
     * The username of the user.
     */
    @JsonProperty("username")
    @JsonView(Views.BothView.class)
    String username;

    /**
     * The team user is member of. This field is only visible in the context of
     * getting list of users.
     */
    @JsonProperty("team")
    @JsonView(Views.ShowTeamsInUsers.class)
    Team team;

    /**
     * The role of the user in the system.
     */
    @JsonProperty("role")
    @JsonView(Views.BothView.class)
    String role;

    /**
     * The token of the user for authentication purposes.
     */
    @JsonProperty("token")
    @JsonView(Views.BothView.class)
    String token;
}
