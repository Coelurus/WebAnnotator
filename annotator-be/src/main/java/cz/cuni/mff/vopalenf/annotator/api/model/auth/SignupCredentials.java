package cz.cuni.mff.vopalenf.annotator.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the credentials required for user signup. This record encapsulates
 * the user's first name, last name, username, and password. It includes
 * validation constraints to ensure that the fields are not blank or empty.
 *
 * @param firstName
 *            The first name of the user. Must not be blank.
 * @param lastName
 *            The last name of the user. Must not be blank.
 * @param username
 *            The username chosen by the user. Must not be blank.
 * @param password
 *            The password chosen by the user. Must not be empty.
 */
public record SignupCredentials(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String username,
        @NotEmpty char[] password) {
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        SignupCredentials that = (SignupCredentials) other;

        if (!username.equals(that.username) || !firstName.equals(that.lastName()) || !lastName.equals(that.lastName()))
            return false;
        return Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName, Arrays.hashCode(password));
    }

    @Override
    public String toString() {
        return String.format("SignupCredentials{username='%s', firstName='%s', lastName='%s', password=%s}", username,
                firstName, lastName, Arrays.toString(password));
    }
}
