package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert ProjectEntity to Project and vice versa.
 */
@Component
public class ProjectMapper {

    private final ModelMapper modelMapper;

    /**
     * Constructor for ProjectMapper.
     *
     * @param modelMapper the ModelMapper instance to use for mapping
     */
    @Autowired
    public ProjectMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Maps a ProjectEntity to a Project without associating it with a Team.
     *
     * @param projectEntity the ProjectEntity to map
     * @return the mapped Project, or null if the input is null
     */
    public Project mapProject(ProjectEntity projectEntity) {
        return mapProject(projectEntity, null);
    }

    /**
     * Maps a ProjectEntity to a Project, associating it with a Team.
     *
     * @param projectEntity the ProjectEntity to map
     * @param team the Team to associate with the Project
     * @return the mapped Project, or null if the input is null
     */
    public Project mapProject(ProjectEntity projectEntity, Team team) {
        if (projectEntity == null) {
            return null;
        }

        return Project.builder()
                .id(projectEntity.getId())
                .projectName(projectEntity.getProjectName())
                .logFileName(projectEntity.getLogFileName())
                .deadline(projectEntity.getDeadline())
                .priority(projectEntity.getPriority().getName())
                .progress(projectEntity.getProgress().getName())
                .team(team)
                .build();
    }

    /**
     * Maps a Project to a ProjectEntity.
     *
     * @param project the Project to map
     * @return the mapped ProjectEntity, or null if the input is null
     */
    public ProjectEntity mapProjectEntity(Project project) {
        if (project == null) {
            return null;
        }

        return modelMapper.map(project, ProjectEntity.ProjectEntityBuilder.class).build();
    }

}
