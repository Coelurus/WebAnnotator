package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Information about position of a hand in a moment of time.
 */
@AllArgsConstructor
@Builder
@Getter
public class LogData {
    private final Double time;
    private final Double posX;
    private final Double posY;
    private final Double posZ;
    @Setter
    private String label;
}
