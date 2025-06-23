package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Represents a priority level for annotations.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Priority {
    /**
     * The value of the priority level. Greater values indicate higher priority.
     */
    @JsonProperty("value")
    int value;
    /**
     * The name of the priority level, e.g., "High", "Medium", "Low".
     */
    @JsonProperty("name")
    String name;
}
