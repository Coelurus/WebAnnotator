package cz.cuni.mff.vopalenf.persistence.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import cz.cuni.mff.vopalenf.persistence.entities.Team;
import lombok.Data;

@Data
public class TeamResponse {
    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    private Long id;

    @JsonProperty("name")
    @JsonView(Views.BothView.class)
    private String name;

    @JsonProperty("leader")
    @JsonView(Views.ShowUsersInTeams.class)
    private UserResponse leader;

    public TeamResponse(Team team) {
        this(team, Views.ShowUsersInTeams.class);
    }

    public TeamResponse(Team team, Class<? extends Views.BothView> view) {
        id = team.getId();
        name = team.getName();

        if (view.equals(Views.ShowUsersInTeams.class)) {
            leader = new UserResponse(team.getLeader(), view);
        }
    }
}
