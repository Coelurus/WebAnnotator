package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cuni.mff.vopalenf.annotator.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Project {

    @JsonProperty("id")
    Long id;

    @JsonProperty("project_name")
    @NotBlank
    String projectName;

    @JsonProperty("log_file_name")
    @NotBlank
    String logFileName;

    @JsonProperty("dead_line")
    LocalDate deadline;

    @JsonProperty("priority")
    Priority priority;

    @JsonProperty("team")
    Team team;

}
