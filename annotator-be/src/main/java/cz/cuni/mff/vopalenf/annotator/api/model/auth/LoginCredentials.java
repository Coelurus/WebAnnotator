package cz.cuni.mff.vopalenf.annotator.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents login credentials for authentication.
 *
 * @param username
 *            the username for login
 * @param password
 *            the password for login, stored as a char array for security
 */
@Builder
public record LoginCredentials(
        /**
         * The username for login.
         */
        @NotBlank String username, /**
                                    * The password for login, stored as a char array for security.
                                    */
        @NotEmpty char[] password) {
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        LoginCredentials that = (LoginCredentials) other;

        if (!username.equals(that.username))
            return false;
        return Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, Arrays.hashCode(password));
    }

    @Override
    public String toString() {
        return "LoginCredentials{" + "username='" + username + '\'' + ", password=" + Arrays.toString(password) + '}';
    }
}
