package cz.cuni.mff.vopalenf.annotator.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import cz.cuni.mff.vopalenf.annotator.api.model.UserResponse;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @JsonView({Views.ShowTeamsInUsers.class})
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(){
        return userService.getAllUsers();
    }


}
