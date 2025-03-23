package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Progress;
import cz.cuni.mff.vopalenf.annotator.enums.ProgressEnum;
import org.springframework.stereotype.Component;

@Component
public class ProgressMapper {

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
