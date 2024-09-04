package cz.cuni.mff.vopalenf.annotator;

import cz.cuni.mff.vopalenf.annotator.storage.StorageProperties;
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
