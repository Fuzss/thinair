package fuzs.thinair.helper;

import net.minecraft.util.StringRepresentable;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe
     */
    GREEN("green", 15, 3, 1),
    /**
     * No loss, no gain
     */
    BLUE("blue", 12, 2, 2),
    /**
     * Slowly lose oxygen
     */
    YELLOW("yellow", 9, 1, 3),
    /**
     * Completely unable to breathe (like underwater)
     */
    RED("red", 6, 0, 4);

    private final String serializedName;
    private final int lightLevel;
    private final int outputSignal;
    private final int bubbleBeats;

    AirQualityLevel(String serializedName, int lightLevel, int outputSignal, int bubbleBeats) {
        this.serializedName = serializedName;
        this.lightLevel = lightLevel;
        this.outputSignal = outputSignal;
        this.bubbleBeats = bubbleBeats;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public boolean bubbleBeats(AirQualityLevel other) {
        return this.bubbleBeats > other.bubbleBeats;
    }
}
