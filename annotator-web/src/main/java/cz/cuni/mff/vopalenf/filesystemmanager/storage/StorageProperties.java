package cz.cuni.mff.vopalenf.filesystemmanager.storage;

import cz.cuni.mff.vopalenf.constants.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines where file system will be created
 */
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Location of a folder where files are stored
     */
    private String location = Constants.FILE_SYSTEM_PATH;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
