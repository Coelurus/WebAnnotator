package cz.cuni.mff.vopalenf.annotator.dao.repository;

import cz.cuni.mff.vopalenf.annotator.dao.model.RefreshTokenEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
    /**
     * Find a refresh token by its token value.
     *
     * @param token the token value to search for
     * @return the refresh token entity if found
     */
    Optional<RefreshTokenEntity> findByToken(String token);
    
    /**
     * Find all refresh tokens for a specific user.
     *
     * @param user the user to search refresh tokens for
     * @return list of refresh tokens for the user
     */
    List<RefreshTokenEntity> findByUser(UserEntity user);
    
    /**
     * Delete all refresh tokens for a specific user.
     *
     * @param user the user whose refresh tokens should be deleted
     */
    void deleteByUser(UserEntity user);
    
    /**
     * Delete all expired refresh tokens.
     *
     * @param now the current date and time
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    /**
     * Revoke all refresh tokens for a specific user.
     *
     * @param userId the ID of the user whose tokens should be revoked
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllUserTokens(Long userId);
}