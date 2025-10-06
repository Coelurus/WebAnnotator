package cz.cuni.mff.vopalenf.annotator.api.controller;

import cz.cuni.mff.vopalenf.annotator.api.model.User;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.AuthResponse;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.LoginCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.RefreshTokenRequest;
import cz.cuni.mff.vopalenf.annotator.api.model.auth.SignupCredentials;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.config.UserAuthProvider;
import cz.cuni.mff.vopalenf.annotator.service.RefreshTokenService;
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

@Tag(name = "Security", description = "Endpoints for handling login, signup, and token refresh")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, UserAuthProvider userAuthProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(summary = "Create new user account and assign tokens", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signup credentials"), responses = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Username already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupCredentials credentials) {
        User user = userService.signup(credentials);
        AuthResponse authResponse = userAuthProvider.createTokens(user);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Login user and assign tokens", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials"), responses = {
            @ApiResponse(responseCode = "200", description = "User successfully logged-in"),
            @ApiResponse(responseCode = "400", description = "Invalid login credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Username not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginCredentials credentials) {
        User user = userService.login(credentials);
        AuthResponse authResponse = userAuthProvider.createTokens(user);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh access token using refresh token", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh token request"), responses = {
            @ApiResponse(responseCode = "200", description = "Token successfully refreshed"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResponse authResponse = userAuthProvider.refreshToken(request.refreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logout user by revoking refresh token", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh token to revoke"), responses = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
        return ResponseEntity.ok().build();
    }

}
