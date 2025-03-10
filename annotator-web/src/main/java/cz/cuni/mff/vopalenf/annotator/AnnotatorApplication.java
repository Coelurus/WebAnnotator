package cz.cuni.mff.vopalenf.annotator;

import cz.cuni.mff.vopalenf.annotator.config.StorageConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
@EnableConfigurationProperties(StorageConfig.class)
@OpenAPIDefinition(
        info = @Info(title = "Annotator", version = "1.0")
)
public class AnnotatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotatorApplication.class, args);
    }

}
