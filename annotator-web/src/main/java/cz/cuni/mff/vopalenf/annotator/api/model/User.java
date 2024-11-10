package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class User {

    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    Long id;

    @JsonProperty("first_name")
    @JsonView(Views.BothView.class)
    String firstName;

    @JsonProperty("last_name")
    @JsonView(Views.BothView.class)
    String lastName;

    @JsonProperty("username")
    @JsonView(Views.BothView.class)
    String userName;

    @JsonProperty("team")
    @JsonView(Views.ShowTeamsInUsers.class)
    Team team;

}
