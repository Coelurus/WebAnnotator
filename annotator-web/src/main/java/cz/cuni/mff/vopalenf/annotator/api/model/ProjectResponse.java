package cz.cuni.mff.vopalenf.annotator.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import cz.cuni.mff.vopalenf.annotator.api.view.Views;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("log_file_name")
    private String logFileName;

    @JsonProperty("dead_line")
    private LocalDate deadline;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("team")
    private TeamResponse team;

    public ProjectResponse(ProjectEntity projectEntity, Class<? extends Views.BothView> view) {
        this.id = projectEntity.getId();
        this.projectName = projectEntity.getProjectName();
        this.logFileName = projectEntity.getLogFileName();
        this.deadline = projectEntity.getDeadline();
        this.priority = projectEntity.getPriority();
        this.team = new TeamResponse(projectEntity.getTeam(), view);
    }
}
