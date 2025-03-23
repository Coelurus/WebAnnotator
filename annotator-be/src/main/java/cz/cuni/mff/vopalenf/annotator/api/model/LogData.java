package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Information about position of a hand in a moment of time.
 */
@Getter
public class LogData {

    private final Double time;
    private final String dimensions;
    private final String type;
    private final boolean running;
    private final Object fTx;
    private final Double posX;
    private final Double posY;
    private final Double posZ;
    private final Double cicS;
    private final Double cicW;
    private final Double cicN;
    private final Double cicE;
    private final Double cicC;
    private final Double sdS;
    private final Double sdW;
    private final Double sdN;
    private final Double sdE;
    private final Double sdC;
    private final Object touch;
    private final Object tap;
    private final Object dblTap;
    private final Object airWheel;
    private final Object gesture;
    @Setter
    private String label;

    public LogData(String label, String logLine) {
        String[] fields = logLine.split("\\s+");

        this.label = label;
        this.time = parseDouble(fields[0]);
        this.dimensions = fields[1];
        this.type = fields[2];
        this.running = Boolean.parseBoolean(fields[3]);
        this.fTx = parseObject(fields[4]);
        this.posX = parseDouble(fields[5]);
        this.posY = parseDouble(fields[6]);
        this.posZ = parseDouble(fields[7]);
        this.cicS = parseDouble(fields[8]);
        this.cicW = parseDouble(fields[9]);
        this.cicN = parseDouble(fields[10]);
        this.cicE = parseDouble(fields[11]);
        this.cicC = parseDouble(fields[12]);
        this.sdS = parseDouble(fields[13]);
        this.sdW = parseDouble(fields[14]);
        this.sdN = parseDouble(fields[15]);
        this.sdE = parseDouble(fields[16]);
        this.sdC = parseDouble(fields[17]);
        this.touch = parseObject(fields[18]);
        this.tap = parseObject(fields[19]);
        this.dblTap = parseObject(fields[20]);
        this.airWheel = parseObject(fields[21]);
        this.gesture = parseObject(fields[22]);
    }

    private Double parseDouble(String value) {
        if (value.equals("-")) {
            return null;
        }
        return Double.parseDouble(value);
    }

    private Object parseObject(String value) {
        if (value.equals("-")) {
            return null;
        }
        return value;
    }
}
