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
    String dimensions;
    String type;
    boolean running;
    Object fTx;
    Double posX;
    Double posY;
    Double posZ;
    Double cicS;
    Double cicW;
    Double cicN;
    Double cicE;
    Double cicC;
    Double sdS;
    Double sdW;
    Double sdN;
    Double sdE;
    Double sdC;
    Object touch;
    Object tap;
    Object dblTap;
    Object airWheel;
    Object gesture;
    String label;
}
