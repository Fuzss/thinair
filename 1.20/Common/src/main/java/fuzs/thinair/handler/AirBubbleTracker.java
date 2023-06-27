package fuzs.thinair.handler;

import com.mojang.datafixers.util.Pair;
import fuzs.thinair.ThinAir;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.helper.AirBubble;
import fuzs.thinair.helper.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

public class AirBubbleTracker {

    private static final Deque<ChunkPos> SERVER_CHUNKS_TO_SCAN = new ArrayDeque<>();

    public static void onBlockChanged(Level world, BlockPos pos, BlockState old, BlockState now) {
        ChunkPos chunkPos = new ChunkPos(pos);
        LevelChunk chunk = world.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        if (chunk != null) {
            Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
            maybeCap.ifPresent(cap -> {
                if (getPlaceBubble(old) != null) {
                    // need to remove this
                    AirBubble removed = cap.getEntries().remove(pos);
                    if (removed == null) {
                        ThinAir.LOGGER.warn("Didn't remove any air bubbles at {}", pos);
                    }
                    chunk.setUnsaved(true);
                }

                Pair<AirQualityLevel, ForgeConfigSpec.DoubleValue> bubble = getPlaceBubble(now);
                if (bubble != null && bubble.getSecond().get() != 0.0) {
                    AirBubble entry = new AirBubble(bubble.getFirst(), bubble.getSecond().get());
                    AirBubble clobbered = cap.getEntries().put(pos, entry);
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
                        recalcChunk(chunk, cap.getEntries());
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

    /**
     * Whether the blockstate projecting a bubble actually provides that bubble.
     * <p>
     * For example soul campfires provide a blue air bubble, but you don't get the air if it's not lit.
     */
    public static boolean canProjectAirBubble(BlockState bs) {
        return (bs.is(Blocks.SOUL_CAMPFIRE) && bs.getValue(CampfireBlock.LIT))
            || bs.is(Blocks.SOUL_FIRE)
            || bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)
            || bs.is(Blocks.NETHER_PORTAL);
    }

    @Nullable
    public static Pair<AirQualityLevel, ForgeConfigSpec.DoubleValue> getPlaceBubble(BlockState bs) {
        if (bs.is(Blocks.SOUL_CAMPFIRE)) {
            return new Pair<>(AirQualityLevel.BLUE, CommonConfig.soulCampfireRange);
        } else if (bs.is(Blocks.SOUL_FIRE)) {
            return new Pair<>(AirQualityLevel.BLUE, CommonConfig.soulFireRange);
        } else if (bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)) {
            return new Pair<>(AirQualityLevel.BLUE, CommonConfig.soulTorchRange);
        } else if (bs.is(Blocks.NETHER_PORTAL)) {
            return new Pair<>(AirQualityLevel.GREEN, CommonConfig.netherPortalRange);
        } else {
            return null;
        }
    }

    public static void recalcChunk(LevelChunk chunk, Map<BlockPos, AirBubble> out) {
        out.clear();

        int minY = chunk.getMinBuildHeight();
        int cornerX = chunk.getPos().getMinBlockX();
        int cornerZ = chunk.getPos().getMinBlockZ();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = cornerX + dx;
                int z = cornerZ + dz;
                for (int y = minY; y < chunk.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, x, z); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState bs = chunk.getBlockState(pos);
                    Pair<AirQualityLevel, ForgeConfigSpec.DoubleValue> bubble = getPlaceBubble(bs);
                    if (bubble != null) {
                        out.put(pos, new AirBubble(bubble.getFirst(), bubble.getSecond().get()));
                    }
                }
            }
        }
    }
}
