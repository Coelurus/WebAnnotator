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

@Builder(toBuilder = true)
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    Long id;

    @JsonProperty("firstName")
    @JsonView(Views.BothView.class)
    String firstName;

    @JsonProperty("lastName")
    @JsonView(Views.BothView.class)
    String lastName;

    @JsonProperty("username")
    @JsonView(Views.BothView.class)
    String username;

    @JsonProperty("team")
    @JsonView(Views.ShowTeamsInUsers.class)
    Team team;

    //@JsonIgnore
    @JsonProperty("password")
    @JsonView(Views.BothView.class)
    String password;

    @JsonProperty("role")
    @JsonView(Views.BothView.class)
    String role;

    @JsonProperty("token")
    @JsonView(Views.BothView.class)
    String token;
}
