package cz.cuni.mff.vopalenf.annotator.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates a ModelMapper bean with custom configuration.
     *
     * @return a configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE)
                .setDestinationNamingConvention((name, type) -> true)
                .setDestinationNameTransformer((name, type) -> name);

        return modelMapper;
    }
}
