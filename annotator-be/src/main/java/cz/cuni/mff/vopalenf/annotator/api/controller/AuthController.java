package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.LoginCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.config.UserAuthProvider;
import cz.cuni.mff.vopalenf.annotator.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Security", description = "Endpoints for handling login and signup")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    public AuthController(
            UserService userService,
            UserAuthProvider userAuthProvider) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @Operation(
            summary = "Create new user account and assign it JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signup credentials"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Username already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody @Valid SignupCredentials credentials) {
        User user = userService.signup(credentials);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Login user and assign them JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully logged-in"),
                    @ApiResponse(responseCode = "400", description = "Invalid login credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Username not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid LoginCredentials credentials) {
        User user = userService.login(credentials);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }

}
