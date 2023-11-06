package fuzs.thinair.capability;

import fuzs.thinair.api.v1.AirQualityLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class AirBubblePositionsCapabilityImpl implements AirBubblePositionsCapability {
    public static final String TAG_POSITIONS = "Positions", TAG_QUALITY = "AirQuality", TAG_SKIP_COUNT_LEFT = "SkipCountLeft";

    private int skipCountLeft;

    private Map<BlockPos, AirQualityLevel> airBubbleEntries = new LinkedHashMap<>();

    @Override
    public Map<BlockPos, AirQualityLevel> getAirBubblePositions() {
        return this.airBubbleEntries;
    }

    @Override
    public int getSkipCountLeft() {
        return this.skipCountLeft;
    }

    @Override
    public void setSkipCountLeft(int skipCountLeft) {
        this.skipCountLeft = skipCountLeft;
    }

    @Override
    public void write(CompoundTag tag) {
        ListTag positions = new ListTag();
        byte[] qualitiesArr = new byte[this.airBubbleEntries.size()];
        int i = 0;
        for (BlockPos pos : this.airBubbleEntries.keySet()) {
            AirQualityLevel airQualityLevel = this.airBubbleEntries.get(pos);
            positions.add(NbtUtils.writeBlockPos(pos));
            qualitiesArr[i] = (byte) airQualityLevel.ordinal();
            i++;
        }
        tag.put(TAG_POSITIONS, positions);
        tag.put(TAG_QUALITY, new ByteArrayTag(qualitiesArr));
        tag.putInt(TAG_SKIP_COUNT_LEFT, this.skipCountLeft);
    }

    @Override
    public void read(CompoundTag tag) {
        ListTag positions = tag.getList(TAG_POSITIONS, Tag.TAG_COMPOUND);
        byte[] qualities = tag.getByteArray(TAG_QUALITY);
        this.airBubbleEntries = new LinkedHashMap<>(positions.size());
        for (int i = 0; i < positions.size(); i++) {
            this.airBubbleEntries.put(NbtUtils.readBlockPos(positions.getCompound(i)), AirQualityLevel.values()[qualities[i]]);
        }
        this.skipCountLeft = tag.getInt(TAG_SKIP_COUNT_LEFT);
    }
}
