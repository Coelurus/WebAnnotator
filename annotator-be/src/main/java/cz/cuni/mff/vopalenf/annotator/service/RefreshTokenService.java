package cz.cuni.mff.vopalenf.annotator.service;

import cz.cuni.mff.vopalenf.annotator.dao.model.RefreshTokenEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.RefreshTokenRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.UserRepository;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service for managing refresh tokens.
 */
@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final int TOKEN_LENGTH = 32;
    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom;

    /**
     * Constructor for RefreshTokenService.
     *
     * @param refreshTokenRepository the repository for refresh tokens
     * @param userRepository the repository for users
     */
    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Creates a new refresh token for the given user.
     *
     * @param username the username to create the refresh token for
     * @return the created refresh token
     */
    @Transactional
    public RefreshTokenEntity createRefreshToken(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found", RefreshTokenService.class.getSimpleName()));

        // Generate a secure random token
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates and returns the refresh token if it exists and is valid.
     *
     * @param token the refresh token to validate
     * @return the refresh token entity
     * @throws BadRequestException if the token is invalid or expired
     */
    public RefreshTokenEntity validateRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token", RefreshTokenService.class.getSimpleName()));

        if (refreshToken.getRevoked()) {
            throw new BadRequestException("Refresh token has been revoked", RefreshTokenService.class.getSimpleName());
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token has expired", RefreshTokenService.class.getSimpleName());
        }

        return refreshToken;
    }

    /**
     * Revokes a refresh token.
     *
     * @param token the token to revoke
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Refresh token not found", RefreshTokenService.class.getSimpleName()));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        
        logger.info("Revoked refresh token for user: {}", refreshToken.getUser().getUsername());
    }

    /**
     * Revokes all refresh tokens for a given user.
     *
     * @param userId the ID of the user whose tokens should be revoked
     */
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
        logger.info("Revoked all refresh tokens for user ID: {}", userId);
    }

    /**
     * Deletes all expired refresh tokens.
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        logger.info("Deleted expired refresh tokens");
    }
}