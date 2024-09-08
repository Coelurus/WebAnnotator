package cz.cuni.mff.vopalenf.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "log_file_name", nullable = false)
    private String logFileName;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "priority")
    private Integer priority;

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
