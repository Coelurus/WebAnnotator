package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

/**
 * Represents a project in the annotation system.
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Project {

    /**
     * The ID of the project.
     */
    @JsonProperty("id")
    Long id;

    /**
     * The name of the project.
     */
    @JsonProperty("projectName")
    @NotBlank
    String projectName;

    /**
     * Name of the logfile containing sensor data to this project.
     */
    @JsonProperty("logFileName")
    @NotBlank
    String logFileName;

    /**
     * Deadline to when the project should be finished.
     */
    @JsonProperty("deadline")
    LocalDate deadline;

    /**
     * Priority of the project.
     */
    @JsonProperty("priority")
    String priority;

    /**
     * Progress in which the project is currently in.
     */
    @JsonProperty("progress")
    String progress;

    /**
     * Team which was assigned to annotate the project.
     */
    @JsonProperty("team")
    Team team;
}
