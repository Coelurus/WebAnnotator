package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrapper class for exporting project data as CSV.
 */
@Data
@AllArgsConstructor
public class ProjectExportWrapper {
    /**
     * Name of the project.
     */
    private String projectName;
    /**
     * CSV data as a string including labels for frames.
     */
    private String csvData;
}
