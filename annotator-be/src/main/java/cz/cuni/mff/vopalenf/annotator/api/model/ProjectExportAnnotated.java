package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.Data;

@Data
public class ProjectExportAnnotated {
    /**
     * Name of the project.
     */
    private String projectName;
    /**
     * CSV data as a string including labels for frames.
     */
    private String csvData;
}
