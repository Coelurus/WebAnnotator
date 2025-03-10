package cz.cuni.mff.vopalenf.annotator.api.model.auth;

public record SignupCredentials(String firstName, String lastName, String username, char[] password) {
}
