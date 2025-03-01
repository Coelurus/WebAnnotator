package cz.cuni.mff.vopalenf.annotator.api.model;

public record SignupCredentials(String firstName, String lastName, String username, char[] password) {
}
