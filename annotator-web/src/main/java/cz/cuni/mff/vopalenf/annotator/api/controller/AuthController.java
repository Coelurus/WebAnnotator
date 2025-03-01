package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.LoginCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.config.UserAuthProvider;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserService userService,
            UserAuthProvider userAuthProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody @Valid SignupCredentials credentials) {
        User user = userService.signup(credentials);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid LoginCredentials credentials) {
        User user = userService.login(credentials);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }

}
