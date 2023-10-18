package fuzs.thinair.handler;

import com.google.common.collect.Queues;
import fuzs.thinair.ThinAir;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.helper.AirBubble;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

public class AirBubbleTracker {
    private static final Deque<ChunkPos> SERVER_CHUNKS_TO_SCAN = Queues.synchronizedDeque(new ArrayDeque<>());

    public static void onBlockChanged(Level world, BlockPos pos, BlockState oldBlockState, BlockState newBlockState) {
        ChunkPos chunkPos = new ChunkPos(pos);
        LevelChunk chunk = world.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        if (chunk != null) {
            Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
            maybeCap.ifPresent(capability -> {
                if (AirQualityLevel.getAirQualityFromBlock(oldBlockState) != null) {
                    // need to remove this
                    AirBubble removed = capability.getAirBubbleEntries().remove(pos);
                    if (removed == null) {
                        ThinAir.LOGGER.warn("Didn't remove any air bubbles at {}", pos);
                    }
                    chunk.setUnsaved(true);
                }

                AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(newBlockState);
                if (airQualityLevel != null) {
                    AirBubble entry = new AirBubble(airQualityLevel);
                    AirBubble clobbered = capability.getAirBubbleEntries().put(pos, entry);
                    if (clobbered != null) {
                        ThinAir.LOGGER.warn("Clobbered air bubble at {}: {}", pos, clobbered);
                    }
                    chunk.setUnsaved(true);
                }
            });
        }
    }

    public static void onChunkUnload(LevelAccessor level, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        LevelChunk chunkFromSource = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
        if (chunkFromSource != null) {
            SERVER_CHUNKS_TO_SCAN.addLast(chunkpos);
        }
    }

    public static void consumeReqdChunksServer(MinecraftServer server, ServerLevel level) {
        if (!SERVER_CHUNKS_TO_SCAN.isEmpty()) {
            ChunkPos chunkpos = SERVER_CHUNKS_TO_SCAN.removeFirst();
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isPresent()) {
                    AirBubblePositionsCapability cap = maybeCap.get();
                    if (cap.getSkipCountLeft() >= 0) {
                        recalculateChunk(chunk, cap.getAirBubbleEntries());
                        chunk.setUnsaved(true);
                        cap.setSkipCountLeft(8);
                    } else {
                        cap.setSkipCountLeft(cap.getSkipCountLeft() - 1);
                    }
                }
            }
        }
    }

    public static void onLevelUnload(MinecraftServer server, LevelAccessor level) {
        SERVER_CHUNKS_TO_SCAN.clear();
    }

    public static void recalculateChunk(LevelChunk chunk, Map<BlockPos, AirBubble> airBubbleEntries) {
        airBubbleEntries.clear();
        int minY = chunk.getMinBuildHeight();
        int cornerX = chunk.getPos().getMinBlockX();
        int cornerZ = chunk.getPos().getMinBlockZ();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = cornerX + dx;
                int z = cornerZ + dz;
                for (int y = minY; y < chunk.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, x, z); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState blockState = chunk.getBlockState(pos);
                    AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(blockState);
                    if (airQualityLevel != null) airBubbleEntries.put(pos, new AirBubble(airQualityLevel));
                }
            }
        }
    }
}
