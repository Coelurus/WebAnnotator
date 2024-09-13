package cz.cuni.mff.vopalenf.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Defines class representing one project that users will have to annotate.
 */
@Entity
@Table(name = "projects")
public class Project {
    /**
     * Identifier of project
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
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
    private Team team;

    public Project() {
    }

    public Project(String projectName, String logFileName, LocalDate deadline, Integer priority, Team team) {
        this.projectName = projectName;
        this.logFileName = logFileName;
        this.deadline = deadline;
        this.priority = priority;
        this.team = team;
    }

    public Long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Integer getPriority() {
        return priority;
    }

    public Team getTeam() {
        return team;
    }
}
