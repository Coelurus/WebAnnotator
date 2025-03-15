package cz.cuni.mff.vopalenf.annotator.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum containing all possible priority levels
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PriorityEnum {
    CRITICAL(4, "critical"),
    HIGH(3, "high"),
    MEDIUM(2, "medium"),
    LOW(1, "low"),
    NONE(0, "none");

    /**
     * Severity of priority as number for convenient comparison
     */
    private final int value;
    /**
     * Human-readable form of priority
     */
    private final String name;

    /**
     * Get Priority object based on its integer representation
     *
     * @param value Integer representation of Priority
     * @return Priority for valid values. {@code NONE} otherwise
     */
    public static PriorityEnum fromValue(int value) {
        return switch (value) {
            case 4 -> CRITICAL;
            case 3 -> HIGH;
            case 2 -> MEDIUM;
            case 1 -> LOW;
            default -> NONE;
        };
    }

    /**
     * Get Priority object based on its name
     *
     * @param name Human-readable name of Priority
     * @return Priority for valid values. {@code NONE} otherwise
     */
    public static PriorityEnum fromName(String name) {
        return switch (name) {
            case "critical" -> CRITICAL;
            case "high" -> HIGH;
            case "medium" -> MEDIUM;
            case "low" -> LOW;
            default -> NONE;
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
