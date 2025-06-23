package cz.cuni.mff.vopalenf.annotator;

import cz.cuni.mff.vopalenf.annotator.config.StorageConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.client.RestTemplate;

/**
 * The main application class for the Annotator service.
 * It initializes the Spring Boot application and configures necessary beans.
 */
@SpringBootApplication
@EnableMethodSecurity
@EnableConfigurationProperties(StorageConfig.class)
@OpenAPIDefinition(
        info = @Info(title = "Annotator", version = "1.0")
)
public class AnnotatorApplication {

    /**
     * The main method to run the Annotator application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AnnotatorApplication.class, args);
    }

    /**
     * Creates a RestTemplate bean for making HTTP requests.
     *
     * @return a RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
