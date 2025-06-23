package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Represents a label used in the annotation system.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Label {

    /**
     * The ID of the label.
     */
    @JsonProperty("id")
    Long id;

    /**
     * Human-readable name of the label.
     */
    @JsonProperty("labelName")
    String labelName;

    /**
     * The color associated with the label, represented as a string.
     */
    @JsonProperty("color")
    String color;
}
