package cz.cuni.mff.vopalenf.annotator.constants;

import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;

/**
 * Stores all constants that are to be used throughout the project
 */
public class Constants {
    public static final String FILE_SYSTEM_PATH = "file_system";
    public static final String ARCHIVE_EXTENSION = ".zip";
    public static final String IMAGE_EXTENSION = ".webp";
    public static final LabelEntity NO_GESTURE = LabelEntity.builder().label("NO_GESTURE").build();
    private Constants() {
        // Prevent instantiation
    }
}
