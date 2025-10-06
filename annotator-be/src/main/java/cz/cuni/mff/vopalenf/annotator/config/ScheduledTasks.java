package cz.cuni.mff.vopalenf.annotator.config;

import cz.cuni.mff.vopalenf.annotator.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration class for scheduled tasks.
 */
@Configuration
@EnableScheduling
public class ScheduledTasks {

    private final RefreshTokenService refreshTokenService;

    /**
     * Constructor for ScheduledTasks.
     *
     * @param refreshTokenService the service for managing refresh tokens
     */
    @Autowired
    public ScheduledTasks(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Scheduled task to clean up expired refresh tokens.
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        refreshTokenService.deleteExpiredTokens();
    }
}