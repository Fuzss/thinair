package fuzs.thinair.helper;

import fuzs.thinair.advancements.AirProtectionSource;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class AirHelper {
    /**
     * Get the "actual" air quality of the location.
     */
    public static AirQualityLevel getAirQualityAtLocation(Vec3 location, Level level) {

        // Let's throw the player a bone and say the best air quality wins
        AirQualityLevel bestAirBubbleQuality = null;
        ChunkPos chunkAtCenter = new ChunkPos(BlockPos.containing(location));
        // max radius for a campfire is 32, so that means we check two chunks on each side.
        $1:
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                ChunkPos posInChunk = new ChunkPos(chunkAtCenter.x + x, chunkAtCenter.z + z);
                LevelChunk chunk = level.getChunkSource().getChunkNow(posInChunk.x, posInChunk.z);
                if (chunk == null) {
                    continue;
                }
                Optional<AirBubblePositionsCapability> maybeCap = ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk);
                if (maybeCap.isEmpty()) {
                    continue;
                }
                AirBubblePositionsCapability capability = maybeCap.get();
                for (BlockPos pos : capability.getAirBubbleEntries().keySet()) {
                    AirBubble entry = capability.getAirBubbleEntries().get(pos);
                    if (bestAirBubbleQuality == null || entry.airQualityLevel().isBetterThan(bestAirBubbleQuality)) {
                        BlockState blockState = level.getBlockState(pos);
                        AirQualityLevel airQualityLevel = AirQualityLevel.getAirQualityFromBlock(blockState);
                        if (airQualityLevel != null) {
                            double distanceSq = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).distanceToSqr(location);
                            if (distanceSq < entry.radius() * entry.radius()) {
                                if (airQualityLevel == AirQualityLevel.GREEN) return AirQualityLevel.GREEN;
                                bestAirBubbleQuality = airQualityLevel;
                            }
                        }
                    }
                }
            }
        }

        if (bestAirBubbleQuality != null) {
            return bestAirBubbleQuality;
        }

        return CommonConfig.getAirQualityAtLevelByDimension(level.dimension().location(), (int) Math.round(location.y));
    }

    public static AirProtectionSource getProtectionFromYellow(LivingEntity entity) {
        AirProtectionSource red = getProtectionFromRed(entity);
        if (red != AirProtectionSource.NONE) {
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

        if (MobEffectUtil.hasWaterBreathing(entity)) {
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
        if (entity.canBreatheUnderwater() || (entity instanceof Player player && player.getAbilities().invulnerable)) {
            return true;
        }
        return !CommonConfig.entitiesAffectedByAirQuality.get().contains(EntityType.getKey(entity.getType()).toString());
    }
}
