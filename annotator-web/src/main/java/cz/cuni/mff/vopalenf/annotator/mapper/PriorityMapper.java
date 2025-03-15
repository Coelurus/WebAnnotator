package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.dao.model.Priority;
import cz.cuni.mff.vopalenf.annotator.enums.PriorityEnum;
import org.springframework.stereotype.Component;

@Component
public class PriorityMapper {

    public Priority mapPriority(PriorityEnum priority) {
        if (priority == null) {
            return null;
        }
        return Priority.builder()
                .value(priority.getValue())
                .name(priority.getName())
                .build();
    }
}
