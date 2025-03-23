package cz.cuni.mff.vopalenf.annotator.dao.model;

import cz.cuni.mff.vopalenf.annotator.enums.PriorityEnum;
import cz.cuni.mff.vopalenf.annotator.enums.ProgressEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Defines class representing one project that users will have to annotate.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
@Getter
@Setter
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
    @Column(name = "deadline", nullable = true)
    private LocalDate deadline;

    /**
     * Information about how important it is to finish this project
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", columnDefinition = "enum")
    private PriorityEnum priority;

    /**
     * Progress of the project
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "progress", columnDefinition = "enum")
    private ProgressEnum progress;

    /**
     * Team to which the project was assigned
     */
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    private TeamEntity team;


}
