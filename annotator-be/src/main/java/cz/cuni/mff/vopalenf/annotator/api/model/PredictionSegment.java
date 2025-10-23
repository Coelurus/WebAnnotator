package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Model representing a predicted gesture segment.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PredictionSegment {
    int start;
    int end;
    String gesture;
}