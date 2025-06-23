package cz.cuni.mff.vopalenf.annotator.mapper;

import cz.cuni.mff.vopalenf.annotator.api.model.Annotation;
import cz.cuni.mff.vopalenf.annotator.dao.model.AnnotationEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert AnnotationEntity to Annotation.
 */
@Component
public class AnnotationMapper {

    /**
     * Maps an AnnotationEntity to an Annotation.
     *
     * @param annotationEntity the AnnotationEntity to map
     * @return the mapped Annotation, or null if the input is null
     */
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
