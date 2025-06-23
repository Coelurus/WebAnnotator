package cz.cuni.mff.vopalenf.annotator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines where file system will be created
 */
@Data
@ConfigurationProperties("storage")
public class StorageConfig {
}
