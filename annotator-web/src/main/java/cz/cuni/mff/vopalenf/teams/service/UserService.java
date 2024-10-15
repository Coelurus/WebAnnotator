package cz.cuni.mff.vopalenf.teams.service;

import cz.cuni.mff.vopalenf.persistence.repositories.UserRepository;
import cz.cuni.mff.vopalenf.persistence.response.UserResponse;
import cz.cuni.mff.vopalenf.persistence.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .map(user -> new UserResponse(user, Views.ShowTeamsInUsers.class))
                        .toList()
        );
    }
}
