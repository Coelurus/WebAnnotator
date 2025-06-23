package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Represents the count of frames in a project.
 */
@Builder
@Value
@AllArgsConstructor
public class FrameCount {
    /**
     * Number of the frames.
     */
    @JsonProperty("count")
    Integer count;
}
