package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Project;
import cz.cuni.mff.vopalenf.annotator.api.model.Team;
import cz.cuni.mff.vopalenf.annotator.dao.model.ProjectEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ProjectMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Project mapProject(ProjectEntity projectEntity) {
        return mapProject(projectEntity, null);
    }

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

    public ProjectEntity mapProjectEntity(Project project) {
        if (project == null) {
            return null;
        }

        return modelMapper.map(project, ProjectEntity.ProjectEntityBuilder.class).build();
    }

}
