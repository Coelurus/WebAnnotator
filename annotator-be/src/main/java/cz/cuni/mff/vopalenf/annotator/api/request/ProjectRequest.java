package cz.cuni.mff.vopalenf.annotator.api.request;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
public class ProjectRequest {
    private String projectName;
    private LocalDate deadline;
    private String priority;
    private String progress;
    @Nullable
    private Long teamId;
    private MultipartFile file;
}
