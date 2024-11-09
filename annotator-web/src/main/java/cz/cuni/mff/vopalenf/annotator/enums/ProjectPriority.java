package cz.cuni.mff.vopalenf.annotator.enums;

public enum ProjectPriority {
    HIGH(2, "high"),
    MEDIUM(1, "medium"),
    LOW(0, "low");

    private final int value;
    private final String name;

    private ProjectPriority(final int value, final String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ProjectPriority fromValue(final int value) {
        return switch (value) {
            case 2 -> HIGH;
            case 1 -> MEDIUM;
            default -> LOW;
        };
    }
}
