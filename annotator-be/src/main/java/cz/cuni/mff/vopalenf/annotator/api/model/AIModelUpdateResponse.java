package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Response model for AI model update operations.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AIModelUpdateResponse {
    String status;
    double accuracy;
    int projects;

}