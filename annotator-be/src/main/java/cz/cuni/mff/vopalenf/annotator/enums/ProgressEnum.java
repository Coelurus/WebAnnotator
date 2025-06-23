package cz.cuni.mff.vopalenf.annotator.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum containing all possible progress levels
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProgressEnum {
    /**
     * Project has not been started yet
     */
    NOT_STARTED(0, "not started"),
    /**
     * Project is in progress
     */
    IN_PROGRESS(1, "in progress"),
    /**
     * Project has been finished
     */
    FINISHED(2, "finished");

    /**
     * Progress represented as a number for convenient usage
     */
    private final int value;

    /**
     * Human-readable form of progress
     */
    private final String name;

    /**
     * Get ProgressEnum object based on its integer representation
     *
     * @param value Integer representation of ProgressEnum
     * @return ProgressEnum for valid values. {@code NOT_STARTED} otherwise
     */
    public static ProgressEnum fromValue(int value) {
        return switch (value) {
            case 2 -> FINISHED;
            case 1 -> IN_PROGRESS;
            default -> NOT_STARTED;
        };
    }

    /**
     * Get ProgressEnum object based on its name
     *
     * @param name Human-readable name of ProgressEnum
     * @return ProgressEnum for valid values. {@code NOT_STARTED} otherwise
     */
    public static ProgressEnum fromName(String name) {
        if (name == null || name.isEmpty()) {
            return NOT_STARTED;
        }
        return switch (name) {
            case "not started" -> NOT_STARTED;
            case "in progress" -> IN_PROGRESS;
            case "finished" -> FINISHED;
            default -> NOT_STARTED;
        };
    }
}
