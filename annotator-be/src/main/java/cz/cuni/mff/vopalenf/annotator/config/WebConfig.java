package cz.cuni.mff.vopalenf.annotator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class for allowing Spring boot to access files from other directories
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Path to the file system directory, defined in application.yaml
     */
    @Value("${app.file-system.path}")
    String fileSystemPath;

    /**
     * Map "/file_system/**" to the file system directory at the root of the project
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + fileSystemPath + "/**")
                .addResourceLocations("file:./" + fileSystemPath + "/");
    }
}
