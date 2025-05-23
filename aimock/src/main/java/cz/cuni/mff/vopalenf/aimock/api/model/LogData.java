package cz.cuni.mff.vopalenf.aimock.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LogData {
    Double time;
    Double posX;
    Double posY;
    Double posZ;
    String label;
}
