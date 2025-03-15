package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("projectName")
    @NotBlank
    String projectName;

    @JsonProperty("logFileName")
    @NotBlank
    String logFileName;

    @JsonProperty("deadline")
    LocalDate deadline;

    @JsonProperty("priority")
    String priority;

    @JsonProperty("progress")
    String progress;

    @JsonProperty("team")
    Team team;

}
