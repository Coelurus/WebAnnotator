package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Label;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

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
