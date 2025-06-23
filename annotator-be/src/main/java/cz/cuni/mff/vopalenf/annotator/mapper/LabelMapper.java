package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert LabelEntity to Label.
 */
@Component
public class LabelMapper {

    /**
     * Maps a LabelEntity to a Label.
     *
     * @param labelEntity the LabelEntity to map
     * @return the mapped Label, or null if the input is null
     */
    public Label mapLabel(LabelEntity labelEntity) {
        if (labelEntity == null) {
            return null;
        }

        return Label.builder()
                .id(labelEntity.getId())
                .labelName(labelEntity.getLabel())
                .color(labelEntity.getColor())
                .build();
    }
}
