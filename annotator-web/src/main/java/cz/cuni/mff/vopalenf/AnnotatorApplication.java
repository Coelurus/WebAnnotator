package cz.cuni.mff.vopalenf;

import cz.cuni.mff.vopalenf.filesystemmanager.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class AnnotatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotatorApplication.class, args);
    }

}
