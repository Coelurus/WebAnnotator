package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Represents a team in the annotation system.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Team {
    /**
     * The ID of the team.
     */
    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    Long id;

    /**
     * The name of the team.
     */
    @JsonProperty("name")
    @JsonView(Views.BothView.class)
    String name;

    /**
     * Leader of the team. This field is only visible in the context of getting list
     * of teams.
     */
    @JsonProperty("leader")
    @JsonView(Views.ShowUsersInTeams.class)
    User leader;

}
