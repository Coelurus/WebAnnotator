package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.request.UserRequest;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserRequest user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already in use");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .passwordHash(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return "Login successful as: " + currentPrincipalName + " with role: " + authentication.getAuthorities().stream().findFirst().get().getAuthority();
    }

}
