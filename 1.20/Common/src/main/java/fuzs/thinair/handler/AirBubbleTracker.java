package fuzs.thinair.handler;

import com.google.common.collect.Queues;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.network.ClientboundChunkAirQualityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
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
    private static final Deque<ChunkPos> CHUNKS_TO_SCAN = Queues.synchronizedDeque(new ArrayDeque<>());

    public static void onBlockStateChange(ServerLevel level, BlockPos pos, BlockState oldBlockState, BlockState newBlockState) {
        ChunkPos chunkPos = new ChunkPos(pos);
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        if (chunk != null) {
            Optional<AirBubblePositionsCapability> optional = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
            optional.ifPresent(capability -> {
                if (AirQualityLevel.getAirQualityFromBlock(oldBlockState) != null) {
                    // need to remove this
                    AirQualityLevel removed = capability.getAirBubblePositions().remove(pos);
                    if (removed == null) {
                        ThinAir.LOGGER.warn("Didn't remove any air bubbles at {}", pos);
                    } else {
                        chunk.setUnsaved(true);
                        ThinAir.NETWORK.sendToAllNear((Vec3i) pos, level, new ClientboundChunkAirQualityMessage(chunk.getPos(), Map.of(pos, removed), ClientboundChunkAirQualityMessage.Mode.REMOVE));
                    }
                }

                AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(newBlockState);
                if (airQualityLevel != null) {
                    AirQualityLevel clobbered = capability.getAirBubblePositions().put(pos, airQualityLevel);
                    if (clobbered != null) {
                        ThinAir.LOGGER.warn("Clobbered air bubble at {}: {}", pos, clobbered);
                    }
                    chunk.setUnsaved(true);
                    ThinAir.NETWORK.sendToAllNear((Vec3i) pos, level, new ClientboundChunkAirQualityMessage(chunk.getPos(), Map.of(pos, airQualityLevel), ClientboundChunkAirQualityMessage.Mode.ADD));
                }
            });
        }
    }

    public static void onChunkLoad(LevelAccessor level, ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        LevelChunk chunkFromSource = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
        if (chunkFromSource != null) {
            CHUNKS_TO_SCAN.addLast(chunkpos);
        }
    }

    public static void onEndLevelTick(MinecraftServer server, ServerLevel level) {
        if (!CHUNKS_TO_SCAN.isEmpty()) {
            ChunkPos chunkpos = CHUNKS_TO_SCAN.removeFirst();
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isPresent()) {
                    AirBubblePositionsCapability capability = maybeCap.get();
                    if (capability.getSkipCountLeft() <= 0) {
                        if (recalculateChunk(chunk, capability.getAirBubblePositions())) {
                            chunk.setUnsaved(true);
                            ThinAir.NETWORK.sendToAllTracking(chunk, new ClientboundChunkAirQualityMessage(chunk.getPos(), capability.getAirBubblePositions(), ClientboundChunkAirQualityMessage.Mode.REPLACE_ALL));
                        }
                        capability.setSkipCountLeft(8);
                    } else {
                        capability.setSkipCountLeft(capability.getSkipCountLeft() - 1);
                    }
                }
            }
        }
    }

    public static void onLevelUnload(MinecraftServer server, LevelAccessor level) {
        CHUNKS_TO_SCAN.clear();
    }

    public static boolean recalculateChunk(LevelChunk chunk, Map<BlockPos, AirQualityLevel> airBubbleEntries) {
        boolean markDirty = !airBubbleEntries.isEmpty();
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
                    if (airQualityLevel != null) {
                        airBubbleEntries.put(pos, airQualityLevel);
                        markDirty = true;
                    }
                }
            }
        }
        return markDirty;
    }

    public static void onChunkWatch(ServerPlayer player, LevelChunk chunk, ServerLevel level) {
        ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk).ifPresent(capability -> {
            ThinAir.NETWORK.sendTo(player, new ClientboundChunkAirQualityMessage(chunk.getPos(), capability.getAirBubblePositions(), ClientboundChunkAirQualityMessage.Mode.REPLACE_ALL));
        });
    }
}
