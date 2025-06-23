package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Represents an annotation returned by this app.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Annotation {
    /**
     * The ID of the project the annotation belongs to.
     */
    @JsonProperty("projectId")
    Long projectId;
    /**
     * The ID of the frame in the project the annotation belongs to.
     */
    @JsonProperty("frameId")
    Long frameId;
    /**
     * The ID of the annotation.
     */
    @JsonProperty("labelId")
    Long labelId;
}
