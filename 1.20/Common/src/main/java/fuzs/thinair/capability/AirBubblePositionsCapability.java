package fuzs.thinair.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.thinair.api.v1.AirQualityLevel;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface AirBubblePositionsCapability extends CapabilityComponent {

    Map<BlockPos, AirQualityLevel> getAirBubblePositions();

    int getSkipCountLeft();

    void setSkipCountLeft(int skipCountLeft);
}
