package cz.cuni.mff.vopalenf.annotator;

import cz.cuni.mff.vopalenf.annotator.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "cz.cuni.mff.vopalenf.persistence.repositories")
@ComponentScan(basePackages = {
        "cz.cuni.mff.vopalenf.annotator",
        "cz.cuni.mff.vopalenf.teams.repositories",
        "cz.cuni.mff.vopalenf.persistence"
})
public class AnnotatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnnotatorApplication.class, args);
    }
}
