package cz.cuni.mff.vopalenf.annotator.api.request;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a request to create or update a team.
 */
@Data
@Builder
public class TeamRequest {
    /**
     * Name of the team.
     */
    private String name;
    /**
     * ID of the leader of the team.
     */
    private Long leaderId;
}
