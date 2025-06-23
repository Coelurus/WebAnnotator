package cz.cuni.mff.vopalenf.annotator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Configuration class for password encoding.
 */
@Component
public class PasswordConfig {

    /**
     * Creates a PasswordEncoder bean using BCrypt hashing algorithm.
     *
     * @return a PasswordEncoder instance configured with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
