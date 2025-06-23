package cz.cuni.mff.vopalenf.annotator.api.request;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a request to create or update a label.
 */
@Data
@Builder
public class LabelRequest {
    /**
     * Name of the label.
     */
    private String labelName;
    /**
     * Color associated with the label, represented as a string in RGB hexadecimal format (e.g., "#FF5733").
     */
    private String color;
}
