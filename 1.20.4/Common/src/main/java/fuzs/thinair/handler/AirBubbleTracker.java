package fuzs.thinair.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.network.ClientboundChunkAirQualityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AirBubbleTracker {
    private static final Set<ChunkPos> CHUNKS_TO_SCAN = Collections.synchronizedSet(Sets.newHashSet());
    private static final List<Map.Entry<ChunkPos, BlockPos>> CHUNK_SCANNING_PROGRESS = Collections.synchronizedList(Lists.newLinkedList());

    public static void onBlockStateChange(ServerLevel level, BlockPos pos, BlockState oldBlockState, BlockState newBlockState) {
        ChunkPos chunkPos = new ChunkPos(pos);
        LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        if (chunk != null && !oldBlockState.is(newBlockState.getBlock())) {
            AirBubblePositionsCapability capability = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.get(chunk);
            if (AirQualityLevel.getAirQualityFromBlock(oldBlockState) != null) {
                // need to remove this
                AirQualityLevel removed = capability.getAirBubblePositions().remove(pos);
                capability.setChanged();
                if (removed == null) {
                    ThinAir.LOGGER.debug("Didn't remove any air bubbles at {}", pos);
                } else {
                    chunk.setUnsaved(true);
                    ThinAir.NETWORK.sendToAllTracking(chunk, new ClientboundChunkAirQualityMessage(chunk.getPos(), Map.of(pos, removed), ClientboundChunkAirQualityMessage.Mode.REMOVE));
                }
            }

            AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(newBlockState);
            if (airQualityLevel != null) {
                AirQualityLevel clobbered = capability.getAirBubblePositions().put(pos, airQualityLevel);
                capability.setChanged();
                if (clobbered != null) {
                    ThinAir.LOGGER.debug("Clobbered air bubble at {}: {}", pos, clobbered);
                }
                chunk.setUnsaved(true);
                ThinAir.NETWORK.sendToAllTracking(chunk, new ClientboundChunkAirQualityMessage(chunk.getPos(), Map.of(pos, airQualityLevel), ClientboundChunkAirQualityMessage.Mode.ADD));
            }
        }
    }

    public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        CHUNKS_TO_SCAN.add(chunk.getPos());
        CHUNK_SCANNING_PROGRESS.add(Map.entry(chunk.getPos(), getChunkStartingPosition(chunk)));
    }

    public static void onChunkUnload(ServerLevel level, LevelChunk chunk) {
        CHUNKS_TO_SCAN.remove(chunk.getPos());
    }

    public static void onChunkWatch(ServerPlayer player, LevelChunk chunk, ServerLevel level) {
        Map<BlockPos, AirQualityLevel> airBubblePositions = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.get(chunk).getAirBubblePositionsView();
        ThinAir.NETWORK.sendTo(player, new ClientboundChunkAirQualityMessage(chunk.getPos(), airBubblePositions, ClientboundChunkAirQualityMessage.Mode.REPLACE));
    }

    public static void onLevelUnload(MinecraftServer server, LevelAccessor level) {
        CHUNKS_TO_SCAN.clear();
        CHUNK_SCANNING_PROGRESS.clear();
    }

    public static void onEndLevelTick(MinecraftServer server, ServerLevel level) {
        if (!CHUNK_SCANNING_PROGRESS.isEmpty()) {
            // the idea is to once in a while sort chunks again so the ones closest to the player are processed first
            // however as chunks are skipped a lot due to ServerChunkCache::getChunkNow returning null this barely has any effect
            if (false || !level.players().isEmpty() && server.getTickCount() % 200 == 0) {
                CHUNK_SCANNING_PROGRESS.sort(Comparator.comparingInt(entry -> {
                    BlockPos worldPosition = entry.getKey().getWorldPosition();
                    return level.players().stream().map(player -> player.chunkPosition().getWorldPosition()).mapToInt(playerPos -> {
                        return (int) playerPos.distSqr(worldPosition);
                    }).min().orElse(0);
                }));
            }
            ListIterator<Map.Entry<ChunkPos, BlockPos>> iterator = CHUNK_SCANNING_PROGRESS.listIterator();
            Map.Entry<ChunkPos, BlockPos> entry = iterator.next();
            ChunkPos chunkPos = entry.getKey();
            if (CHUNKS_TO_SCAN.contains(chunkPos)) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
                if (chunk != null) {
                    AirBubblePositionsCapability capability = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.get(chunk);
                    // this should be 8, but is disabled right now as the scanning process is very unreliable and skipping a chunk a few times won't help with all that at the moment
                    // it was broken in the original mod anyway and would run every tick as well
                    if (true || capability.getSkipCountLeft() <= 0) {
                        capability.setSkipCountLeft(8);
                        HashMap<BlockPos, AirQualityLevel> airBubblePositions = Maps.newHashMap();
                        BlockPos blockPos = collectAirQualityPositions(chunk, entry.getValue(), airBubblePositions);
                        boolean markDirty = false;
                        if (entry.getValue().equals(getChunkStartingPosition(chunk))) {
                            capability.getAirBubblePositions().clear();
                            capability.getAirBubblePositions().putAll(airBubblePositions);
                            capability.setChanged();
                            ThinAir.NETWORK.sendToAllTracking(chunk, new ClientboundChunkAirQualityMessage(chunkPos, airBubblePositions, ClientboundChunkAirQualityMessage.Mode.REPLACE));
                            markDirty = true;
                        } else if (!airBubblePositions.isEmpty()) {
                            capability.getAirBubblePositions().putAll(airBubblePositions);
                            capability.setChanged();
                            ThinAir.NETWORK.sendToAllTracking(chunk, new ClientboundChunkAirQualityMessage(chunkPos, airBubblePositions, ClientboundChunkAirQualityMessage.Mode.ADD));
                            markDirty = true;
                        }
                        if (markDirty) {
                            chunk.setUnsaved(true);
                        }
                        if (blockPos != null) {
                            iterator.set(Map.entry(chunkPos, blockPos));
                            return;
                        }
                    } else {
                        capability.setSkipCountLeft(capability.getSkipCountLeft() - 1);
                    }
                } else {
                    return;
                }
            }
            iterator.remove();
            CHUNKS_TO_SCAN.remove(chunkPos);
        }
    }

    private static BlockPos getChunkStartingPosition(LevelChunk chunk) {
        int posX = chunk.getPos().getMinBlockX();
        int posY = chunk.getMinBuildHeight();
        int posZ = chunk.getPos().getMinBlockZ();
        return new BlockPos(posX, posY, posZ);
    }

    @Nullable
    private static BlockPos collectAirQualityPositions(LevelChunk chunk, BlockPos startingPosition, Map<BlockPos, AirQualityLevel> airBubbleEntries) {
        int minX = chunk.getPos().getMinBlockX();
        int minY = chunk.getMinBuildHeight();
        int minZ = chunk.getPos().getMinBlockZ();
        int startX = startingPosition.getX() - minX;
        int startY = startingPosition.getY();
        int startZ = startingPosition.getZ() - minZ;
        int iterations = 0;
        for (int dx = startX; dx < 16; dx++, startX = 0) {
            for (int dz = startZ; dz < 16; dz++, startZ = 0) {
                int posX = minX + dx;
                int posZ = minZ + dz;
                int maxY = chunk.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, posX, posZ);
                for (int posY = startY; posY < maxY; posY++, startY = minY, iterations++) {
                    BlockPos blockPos = new BlockPos(posX, posY, posZ);
                    // this is disabled right now as it does not work properly
                    // the chunk will not be found again on the next server tick as ServerChunkCache::getChunkNow returns null quite a lot
                    if (iterations >= 98304) {
                        return blockPos;
                    }
                    BlockState blockState = chunk.getBlockState(blockPos);
                    AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(blockState);
                    if (airQualityLevel != null) {
                        airBubbleEntries.put(blockPos, airQualityLevel);
                    }
                }
            }
        }
        return null;
    }
}
