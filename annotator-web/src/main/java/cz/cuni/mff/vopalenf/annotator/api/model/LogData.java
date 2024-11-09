package cz.cuni.mff.vopalenf.annotator.api.model;

/**
 * Information about position of a hand in a moment of time.
 */
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

    public LogData(String logLine) {
        String[] fields = logLine.split("\\s+");

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

    public Double getTime() {
        return time;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getType() {
        return type;
    }

    public boolean isRunning() {
        return running;
    }

    public Object getfTx() {
        return fTx;
    }

    public Double getPosX() {
        return posX;
    }

    public Double getPosY() {
        return posY;
    }

    public Double getPosZ() {
        return posZ;
    }

    public Double getCicS() {
        return cicS;
    }

    public Double getCicW() {
        return cicW;
    }

    public Double getCicN() {
        return cicN;
    }

    public Double getCicE() {
        return cicE;
    }

    public Double getCicC() {
        return cicC;
    }

    public Double getSdS() {
        return sdS;
    }

    public Double getSdW() {
        return sdW;
    }

    public Double getSdN() {
        return sdN;
    }

    public Double getSdE() {
        return sdE;
    }

    public Double getSdC() {
        return sdC;
    }

    public Object getTouch() {
        return touch;
    }

    public Object getTap() {
        return tap;
    }

    public Object getDblTap() {
        return dblTap;
    }

    public Object getAirWheel() {
        return airWheel;
    }

    public Object getGesture() {
        return gesture;
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
