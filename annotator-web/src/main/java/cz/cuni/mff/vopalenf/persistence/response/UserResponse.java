package cz.cuni.mff.vopalenf.persistence.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.persistence.entities.User;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import lombok.Data;

@Data
public class UserResponse {

    @JsonProperty("id")
    @JsonView(Views.BothView.class)
    private Long id;

    @JsonProperty("first_name")
    @JsonView(Views.BothView.class)
    private String firstName;

    @JsonProperty("last_name")
    @JsonView(Views.BothView.class)
    private String lastName;

    @JsonProperty("username")
    @JsonView(Views.BothView.class)
    private String userName;

    @JsonProperty("team")
    @JsonView(Views.ShowTeamsInUsers.class)
    private TeamResponse team;

    public UserResponse(User user) {
        this(user, Views.ShowTeamsInUsers.class);
    }

    public UserResponse(User user, Class<? extends Views.BothView> view) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();

        if (view.equals(Views.ShowTeamsInUsers.class)) {
            this.team = new TeamResponse(user.getTeam(), view);
        }
    }
}
