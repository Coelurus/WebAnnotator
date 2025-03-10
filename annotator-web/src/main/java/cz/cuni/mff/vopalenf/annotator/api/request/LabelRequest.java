package cz.cuni.mff.vopalenf.annotator.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabelRequest {
    private String labelName;
    private String color;
}
