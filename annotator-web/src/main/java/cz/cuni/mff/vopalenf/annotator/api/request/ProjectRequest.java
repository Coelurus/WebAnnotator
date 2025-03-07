package cz.cuni.mff.vopalenf.annotator.api.request;

import cz.cuni.mff.vopalenf.annotator.enums.Priority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
public class ProjectRequest {
    private String projectName;
    private LocalDate deadline;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private Long teamId;
    private MultipartFile file;
}
