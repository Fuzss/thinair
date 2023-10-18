package fuzs.thinair.client.handler;

import com.google.common.collect.Queues;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.handler.ServerAirBubbleTracker;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ClientAirBubbleTracker {
    private static final Deque<ChunkPos> CLIENT_CHUNKS_TO_SCAN = Queues.synchronizedDeque(new ArrayDeque<>());

    public static void onChunkLoad(LevelAccessor level, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        LevelChunk chunkFromSource = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
        if (chunkFromSource != null) {
            CLIENT_CHUNKS_TO_SCAN.addLast(chunkpos);
        }
    }

    public static void onEndLevelTick(Minecraft minecraft, ClientLevel level) {
        if (!CLIENT_CHUNKS_TO_SCAN.isEmpty()) {
            ChunkPos chunkpos = CLIENT_CHUNKS_TO_SCAN.removeFirst();
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isPresent()) {
                    AirBubblePositionsCapability capability = maybeCap.get();
                    if (capability.getSkipCountLeft() >= 0) {
                        ServerAirBubbleTracker.recalculateChunk(chunk, capability.getAirBubblePositions());
                        chunk.setUnsaved(true);
                        capability.setSkipCountLeft(8);
                    } else {
                        capability.setSkipCountLeft(capability.getSkipCountLeft() - 1);
                    }
                }
            }
        }
    }

    public static void onLevelUnload(Minecraft client, LevelAccessor level) {
        CLIENT_CHUNKS_TO_SCAN.clear();
    }
}
