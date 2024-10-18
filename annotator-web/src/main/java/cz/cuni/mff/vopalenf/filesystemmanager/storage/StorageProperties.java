package cz.cuni.mff.vopalenf.filesystemmanager.storage;

import cz.cuni.mff.vopalenf.constants.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines where file system will be created
 */
@Data
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Location of a folder where files are stored
     */
    private String location = Constants.FILE_SYSTEM_PATH;
}
