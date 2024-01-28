package fuzs.thinair.capability;

import com.google.common.collect.Maps;
import fuzs.thinair.helper.AirBubble;
import fuzs.thinair.helper.AirQualityLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores positions of air bubble projections as a struct of arrays
 */
public class AirBubblePositionsCapabilityImpl implements AirBubblePositionsCapability {
    public static final String CAP_NAME = "air_bubble_positions";
    public static final String TAG_POSITIONS = "positions", TAG_QUALITY = "air_quality", TAG_RADIUS = "radius";
    public static final String TAG_SKIP_COUNT_LEFT = "skip_count_left";

    private Map<BlockPos, AirBubble> entries = Maps.newHashMap();
    // Number of times we will skip rescanning this.
    private int skipCountLeft;

    public Map<BlockPos, AirBubble> getEntries() {
        return this.entries;
    }

    public int getSkipCountLeft() {
        return this.skipCountLeft;
    }

    public void setSkipCountLeft(int skipCountLeft) {
        this.skipCountLeft = skipCountLeft;
    }

    @Override
    public void write(CompoundTag tag) {
        ListTag positions = new ListTag();
        byte[] qualitiesArr = new byte[this.entries.size()];
        long[] radiusesArr = new long[this.entries.size()];

        int i = 0;
        for (BlockPos pos : this.entries.keySet()) {
            AirBubble entry = this.entries.get(pos);
            positions.add(NbtUtils.writeBlockPos(pos));
            qualitiesArr[i] = (byte) entry.airQuality().ordinal();
            radiusesArr[i] = Double.doubleToRawLongBits(entry.radius());
            i++;
        }

        tag.put(TAG_POSITIONS, positions);
        tag.put(TAG_QUALITY, new ByteArrayTag(qualitiesArr));
        tag.put(TAG_RADIUS, new LongArrayTag(radiusesArr));
        tag.putInt(TAG_SKIP_COUNT_LEFT, this.skipCountLeft);

    }

    @Override
    public void read(CompoundTag tag) {
        ListTag positions = tag.getList(TAG_POSITIONS, Tag.TAG_COMPOUND);
        byte[] qualities = tag.getByteArray(TAG_QUALITY);
        long[] radiuses = tag.getLongArray(TAG_RADIUS);

        this.entries = new HashMap<>(positions.size());
        for (int i = 0; i < positions.size(); i++) {
            this.entries.put(NbtUtils.readBlockPos(positions.getCompound(i)),
                    new AirBubble(AirQualityLevel.values()[qualities[i]], Double.longBitsToDouble(radiuses[i])));
        }

        this.skipCountLeft = tag.getInt(TAG_SKIP_COUNT_LEFT);
    }
}
