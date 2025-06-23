package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.Data;

/**
 * A class representing a prediction by assigning concrete frame in a project a
 * label.
 */
@Data
public class PredictionTriple {
    /**
     * The ID of the project to which the frame belongs.
     */
    public final Long projectId;
    /**
     * The ID of the frame in the project.
     */
    public final Long frameId;
    /**
     * The label assigned by AI to the frame in the project.
     */
    public final String label;
}
