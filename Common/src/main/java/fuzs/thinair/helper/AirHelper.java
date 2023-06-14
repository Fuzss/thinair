package fuzs.thinair.helper;

import com.mojang.datafixers.util.Pair;
import fuzs.thinair.advancements.AirProtectionSource;
import fuzs.thinair.advancements.AirSource;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.handler.AirBubbleTracker;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class AirHelper {
    /**
     * Get the "actual" air quality of the location.
     */
    public static Pair<AirQualityLevel, AirSource> getO2LevelFromLocation(Vec3 position, Level world) {
        // do we allow soul campfires to override being underwater?
        if (isInFluid(position, world)) {
            return new Pair<>(AirQualityLevel.RED, AirSource.FLUID);
        }

        // Let's throw the player a bone and say the best air quality wins
        Pair<AirQualityLevel, AirSource> bestAirBubbleQuality = null;
        ChunkPos centerChunkPos = new ChunkPos(new BlockPos(position));
        // max radius for a campfire is 32, so that means we check two chunks on each side.
        int chunkRadius = 2;
        outer:
        for (int cdx = -chunkRadius; cdx <= chunkRadius; cdx++) {
            for (int cdz = -chunkRadius; cdz <= chunkRadius; cdz++) {
                ChunkPos chunkpos = new ChunkPos(centerChunkPos.x + cdx, centerChunkPos.z + cdz);
                LevelChunk chunk = world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
                if (chunk == null) {
                    continue;
                }
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isEmpty()) {
                    continue;
                }
                AirBubblePositionsCapability cap = maybeCap.get();
                for (BlockPos pos : cap.getEntries().keySet()) {
                    AirBubble entry = cap.getEntries().get(pos);
                    if (bestAirBubbleQuality == null
                        || !bestAirBubbleQuality.getFirst().bubbleBeats(entry.airQuality())) {
                        BlockState bs = world.getBlockState(pos);
                        double distSq = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                            .distanceToSqr(position);
                        if (AirBubbleTracker.canProjectAirBubble(bs) && distSq < entry.radius() * entry.radius()) {
                            // we made it!
                            AirSource source = AirSource.OTHER;
                            if (bs.is(Blocks.SOUL_CAMPFIRE)
                                || bs.is(Blocks.SOUL_FIRE)
                                || bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)) {
                                source = AirSource.SOUL;
                            } else if (bs.is(Blocks.LAVA)) {
                                source = AirSource.LAVA;
                            } else if (bs.is(Blocks.NETHER_PORTAL)) {
                                source = AirSource.NETHER_PORTAL;
                            }
                            bestAirBubbleQuality = new Pair<>(entry.airQuality(), source);
                            if (entry.airQuality() == AirQualityLevel.RED) {
                                // nothing can get worse than red, so just stop
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        if (bestAirBubbleQuality != null) {
            return bestAirBubbleQuality;
        }

        ResourceLocation dim = world.dimension().location();
        AirQualityLevel quality = CommonConfig.getAirQualityAtLevelByDimension(dim, (int) Math.round(position.y));
        return new Pair<>(quality, AirSource.DIMENSION);
    }

    public static boolean isInFluid(Vec3 position, Level world) {
        BlockState blockAtEyes = world.getBlockState(new BlockPos(position));
        return !blockAtEyes.getFluidState().isEmpty() && !blockAtEyes.is(Blocks.BUBBLE_COLUMN);
    }

    public static AirProtectionSource getProtectionFromYellow(LivingEntity entity) {
        AirProtectionSource red = getProtectionFromRed(entity);
        if (red != null) {
            return red;
        }

        Iterable<ItemStack> armorItems = entity.getArmorSlots();
        for (ItemStack item : armorItems) {
            if (item.is(ModRegistry.RESPIRATOR_ITEM.get())) {
                return AirProtectionSource.RESPIRATOR;
            }
        }

        return AirProtectionSource.NONE;
    }

    public static AirProtectionSource getProtectionFromRed(LivingEntity entity) {
        if (canAlwaysBreathe(entity)) {
            return AirProtectionSource.INHERENT;
        }

        if (entity.hasEffect(MobEffects.WATER_BREATHING)) {
            return AirProtectionSource.WATER_BREATHING;
        }

        Optional<AirProtectionCapability> maybeCap = ModRegistry.AIR_PROTECTION_CAPABILITY.maybeGet(entity);
        if (maybeCap.isPresent()) {
            AirProtectionCapability cap = maybeCap.get();
            if (cap.isProtected()) {
                return AirProtectionSource.BLADDER;
            }
        }

        return AirProtectionSource.NONE;
    }

    public static boolean canAlwaysBreathe(LivingEntity entity) {
        if (entity.canBreatheUnderwater()
            || (entity instanceof Player player && player.getAbilities().invulnerable)) {
            return true;
        }
        List<? extends String> alwaysOkEntities = CommonConfig.alwaysBreathingEntities.get();
        return alwaysOkEntities.contains(EntityType.getKey(entity.getType()).toString());
    }
}
