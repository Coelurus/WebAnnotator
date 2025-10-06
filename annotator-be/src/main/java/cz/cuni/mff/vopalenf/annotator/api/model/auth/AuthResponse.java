package cz.cuni.mff.vopalenf.annotator.api.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model containing both access token and refresh token.
 */
public record AuthResponse(
    /**
     * The access token (JWT).
     */
    @JsonProperty("accessToken")
    String accessToken,
    
    /**
     * The refresh token.
     */
    @JsonProperty("refreshToken")
    String refreshToken,
    
    /**
     * Token type (always "Bearer").
     */
    @JsonProperty("tokenType")
    String tokenType
) {
    /**
     * Creates an AuthResponse with Bearer token type.
     *
     * @param accessToken the JWT access token
     * @param refreshToken the refresh token
     * @return the auth response
     */
    public static AuthResponse of(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }
}