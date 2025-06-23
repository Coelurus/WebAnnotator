package cz.cuni.mff.vopalenf.annotator.api.request;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * Represents a request to create or update a project.
 */
@Data
@Builder
public class ProjectRequest {
    /**
     * Name of the project.
     */
    private String projectName;
    /**
     * Date when the project should be finished.
     */
    private LocalDate deadline;
    /**
     * Priority of the project.
     */
    private String priority;
    /**
     * State in which the project is currently in.
     */
    private String progress;
    /**
     * ID of the team to which the project belongs. Can be null if the project is
     * not associated with any team.
     */
    @Nullable
    private Long teamId;
    /**
     * Zip file containing the log file with sensor data and frames captured by the
     * camera.
     */
    private MultipartFile file;
}
