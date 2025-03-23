package cz.cuni.mff.vopalenf.annotator.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;
import java.util.Objects;

public record SignupCredentials(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String username,
        @NotEmpty
        char[] password
) {
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

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
        return String.format("SignupCredentials{username='%s', firstName='%s', lastName='%s', password=%s}", username, firstName, lastName, Arrays.toString(password));
    }
}
