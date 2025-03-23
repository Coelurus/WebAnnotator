package cz.cuni.mff.vopalenf.annotator.config;

import cz.cuni.mff.vopalenf.annotator.constants.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines where file system will be created
 */
@Data
@ConfigurationProperties("storage")
public class StorageConfig {

    /**
     * Location of a folder where files are stored
     */
    private String location = Constants.FILE_SYSTEM_PATH;
}
