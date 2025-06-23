package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Represents the progress of work on a project.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Progress {
    /**
     * ID indicating the {@code ProgressEnum}.
     */
    @JsonProperty("value")
    int value;

    /**
     * Human-readable name of the progress.
     */
    @JsonProperty("name")
    String name;
}
