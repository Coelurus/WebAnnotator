package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
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

    public UserResponse(UserEntity userEntity) {
        this(userEntity, Views.ShowTeamsInUsers.class);
    }

    public UserResponse(UserEntity userEntity, Class<? extends Views.BothView> view) {
        this.id = userEntity.getId();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.userName = userEntity.getUserName();

        if (view.equals(Views.ShowTeamsInUsers.class)) {
            this.team = new TeamResponse(userEntity.getTeam(), view);
        }
    }
}
