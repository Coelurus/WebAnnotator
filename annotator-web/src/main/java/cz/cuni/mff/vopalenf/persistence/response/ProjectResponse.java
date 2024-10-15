package cz.cuni.mff.vopalenf.persistence.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cuni.mff.vopalenf.persistence.entities.Project;
import cz.cuni.mff.vopalenf.persistence.view.Views;
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

    public ProjectResponse(Project project, Class<? extends Views.BothView> view) {
        this.id = project.getId();
        this.projectName = project.getProjectName();
        this.logFileName = project.getLogFileName();
        this.deadline = project.getDeadline();
        this.priority = project.getPriority();
        this.team = new TeamResponse(project.getTeam(), view);
    }
}
