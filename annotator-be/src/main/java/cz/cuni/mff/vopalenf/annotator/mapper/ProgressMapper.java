package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Progress;
import cz.cuni.mff.vopalenf.annotator.enums.ProgressEnum;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert ProgressEnum to Progress.
 */
@Component
public class ProgressMapper {

    /**
     * Maps a ProgressEnum to a Progress.
     *
     * @param progress the ProgressEnum to map
     * @return the mapped Progress, or null if the input is null
     */
    public Progress mapProgress(ProgressEnum progress) {
        if (progress == null) {
            return null;
        }
        return Progress.builder()
                .value(progress.getValue())
                .name(progress.getName())
                .build();
    }
}
