package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import org.springframework.stereotype.Component;

@Component
public class AnnotationMapper {

    public Annotation mapAnnotation(AnnotationEntity annotationEntity) {
        if (annotationEntity == null) {
            return null;
        }

        return Annotation.builder()
                .projectId(annotationEntity.getProjectId())
                .frameId(annotationEntity.getFrameId())
                .labelId(annotationEntity.getLabelId())
                .build();
    }
}
