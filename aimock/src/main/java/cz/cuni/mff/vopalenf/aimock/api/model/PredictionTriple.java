package cz.cuni.mff.vopalenf.aimock.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictionTriple {

    public Long projectId;

    public Long frameId;

    public String label;
}
