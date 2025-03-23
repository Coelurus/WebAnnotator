package cz.cuni.mff.vopalenf.annotator.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamRequest {
    private String name;
    private Long leaderId;
}
