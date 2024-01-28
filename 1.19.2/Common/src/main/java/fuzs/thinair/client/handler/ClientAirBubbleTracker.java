package fuzs.thinair.client.handler;

import fuzs.thinair.handler.AirBubbleTracker;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ClientAirBubbleTracker {
    // Two lists because in a singleplayer world these will be on the same JVM
    private static final Deque<ChunkPos> CLIENT_CHUNKS_TO_SCAN = new ArrayDeque<>();

    public static void onChunkLoadClient(LevelAccessor level, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        LevelChunk chunkFromSource = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
        if (chunkFromSource != null) {
            CLIENT_CHUNKS_TO_SCAN.addLast(chunkpos);
        }
    }

    public static void consumeReqdChunksClient(Minecraft client) {
        // TODO replace this with level tick end, which is not available in Forge on 1.19.2 (TickEvents$LevelTickEvent only fires for server level)
        if (!CLIENT_CHUNKS_TO_SCAN.isEmpty()) {
            ChunkPos chunkpos = CLIENT_CHUNKS_TO_SCAN.removeFirst();
            LevelChunk chunk = client.level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isPresent()) {
                    AirBubblePositionsCapability cap = maybeCap.get();
                    if (cap.getSkipCountLeft() >= 0) {
                        AirBubbleTracker.recalcChunk(chunk, cap.getEntries());
                        chunk.setUnsaved(true);
                        cap.setSkipCountLeft(8);
                    } else {
                        cap.setSkipCountLeft(cap.getSkipCountLeft() - 1);
                    }
                }
            }
        }
    }

    public static void onWorldClose(Minecraft client, LevelAccessor level) {
        CLIENT_CHUNKS_TO_SCAN.clear();
    }
}
