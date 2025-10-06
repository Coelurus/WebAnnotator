package cz.cuni.mff.vopalenf.annotator.api.model.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request model for refresh token operations.
 */
public record RefreshTokenRequest(
    /**
     * The refresh token to use for generating a new access token.
     */
    @NotBlank(message = "Refresh token cannot be blank")
    String refreshToken
) {
}