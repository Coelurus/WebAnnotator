package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Priority;
import cz.cuni.mff.vopalenf.annotator.enums.PriorityEnum;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert PriorityEnum to Priority.
 */
@Component
public class PriorityMapper {

    /**
     * Maps a PriorityEnum to a Priority.
     *
     * @param priority
     *            the PriorityEnum to map
     * @return the mapped Priority, or null if the input is null
     */
    public Priority mapPriority(PriorityEnum priority) {
        if (priority == null) {
            return null;
        }
        return Priority.builder().value(priority.getValue()).name(priority.getName()).build();
    }
}
