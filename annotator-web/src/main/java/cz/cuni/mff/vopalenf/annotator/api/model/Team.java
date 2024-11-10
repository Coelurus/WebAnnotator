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
public class Team {
    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    Long id;

    @JsonProperty("name")
    @JsonView(Views.BothView.class)
    String name;

    @JsonProperty("leader")
    @JsonView(Views.ShowUsersInTeams.class)
    User leader;
    
}
