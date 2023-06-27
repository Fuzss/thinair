package fuzs.thinair.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.thinair.helper.AirBubble;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface AirBubblePositionsCapability extends CapabilityComponent {

    Map<BlockPos, AirBubble> getEntries();

    int getSkipCountLeft();

    void setSkipCountLeft(int skipCountLeft);
}
