package cz.cuni.mff.vopalenf.annotator.config;

import cz.cuni.mff.vopalenf.annotator.constants.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class for allowing Spring boot to access files from other directories
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Map "/file_system/**" to the file system directory at the root of the project
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + Constants.FILE_SYSTEM_PATH + "/**")
                .addResourceLocations("file:./" + Constants.FILE_SYSTEM_PATH + "/");
    }
}
