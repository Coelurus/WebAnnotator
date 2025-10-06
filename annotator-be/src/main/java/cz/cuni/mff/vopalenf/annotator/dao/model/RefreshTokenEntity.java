package cz.cuni.mff.vopalenf.annotator.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a refresh token used for JWT token renewal.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshTokenEntity {

    /**
     * ID of the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The refresh token value.
     */
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    /**
     * The user associated with this refresh token.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * The expiration date and time of the refresh token.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * The creation date and time of the refresh token.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Whether the refresh token has been revoked.
     */
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;
}