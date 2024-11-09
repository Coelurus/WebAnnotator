package cz.cuni.mff.vopalenf.annotator.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Defines class representing one project that users will have to annotate.
 */
@Data
@Entity
@Table(name = "projects")
public class ProjectEntity {
    /**
     * Identifier of project
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Name of the project
     */
    @Column(name = "project_name", nullable = false)
    private String projectName;

    /**
     * Name of the log file that was generated for this project. It is also the name of folder in file-system
     * where data for this project are stored.
     */
    @Column(name = "log_file_name", nullable = false)
    private String logFileName;

    /**
     * Date till which to project should be annotated.
     */
    @Column(name = "deadline")
    private LocalDate deadline;

    /**
     * Information about how important it is to finish this project
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * Team to which the project was assigned
     */
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    public ProjectEntity() {
    }

    public ProjectEntity(String projectName, String logFileName, LocalDate deadline, Integer priority, TeamEntity team) {
        this.projectName = projectName;
        this.logFileName = logFileName;
        this.deadline = deadline;
        this.priority = priority;
        this.team = team;
    }
}
