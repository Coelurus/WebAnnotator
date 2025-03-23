package cz.cuni.mff.vopalenf.annotator.util;

import cz.cuni.mff.vopalenf.annotator.dao.model.ColorEntity;
import cz.cuni.mff.vopalenf.annotator.dao.model.LabelEntity;
import cz.cuni.mff.vopalenf.annotator.dao.repository.ColorRepository;
import cz.cuni.mff.vopalenf.annotator.dao.repository.LabelRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ColorUtil {
    private static final String DEFAULT_COLOR = "black";
    private final LabelRepository labelRepository;
    private final ColorRepository colorRepository;

    public ColorUtil(LabelRepository labelRepository,
                     ColorRepository colorRepository) {
        this.labelRepository = labelRepository;
        this.colorRepository = colorRepository;
    }

    /**
     * Find color that has been used the least as label color
     *
     * @return String name of color that has been used the least
     */
    public String findLeastUsedColor() {
        Map<String, Long> colorUsage = labelRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(LabelEntity::getColor, Collectors.counting()));

        List<String> allColors = colorRepository.findAll()
                .stream()
                .map(ColorEntity::getName).toList();

        allColors.forEach(color -> colorUsage.putIfAbsent(color, 0L));

        return colorUsage.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(DEFAULT_COLOR);
    }
}
