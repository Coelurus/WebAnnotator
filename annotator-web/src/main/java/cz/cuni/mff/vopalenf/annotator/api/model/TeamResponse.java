package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.dao.model.TeamEntity;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
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

    public TeamResponse(TeamEntity teamEntity) {
        this(teamEntity, Views.ShowUsersInTeams.class);
    }

    public TeamResponse(TeamEntity teamEntity, Class<? extends Views.BothView> view) {
        id = teamEntity.getId();
        name = teamEntity.getName();

        if (view.equals(Views.ShowUsersInTeams.class)) {
            leader = new UserResponse(teamEntity.getLeader(), view);
        }
    }
}
