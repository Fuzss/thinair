package fuzs.thinair.helper;

import fuzs.thinair.api.AirQualityHelper;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class AirQualityHelperImpl implements AirQualityHelper {

    @Override
    public AirQualityLevel getAirQualityAtLocation(Level level, Vec3 location) {

        BlockState blockAtEyes = level.getBlockState(BlockPos.containing(location));
        AirQualityLevel airQualityAtEyes = AirQualityLevel.getAirQualityAtEyes(blockAtEyes);
        if (airQualityAtEyes != null) return airQualityAtEyes;

        // Let's throw the player a bone and say the best air quality wins
        AirQualityLevel bestAirBubbleQuality = null;
        ChunkPos chunkAtCenter = new ChunkPos(BlockPos.containing(location));
        // max radius for a campfire is 32, so that means we check two chunks on each side.
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                ChunkPos posInChunk = new ChunkPos(chunkAtCenter.x + x, chunkAtCenter.z + z);
                Optional<AirBubblePositionsCapability> optional = Optional.ofNullable(level.getChunkSource().getChunkNow(posInChunk.x, posInChunk.z)).flatMap(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY::maybeGet);
                if (optional.isPresent()) {
                    AirBubblePositionsCapability capability = optional.get();
                    for (BlockPos pos : capability.getAirBubblePositions().keySet()) {
                        AirQualityLevel airQualityLevel = capability.getAirBubblePositions().get(pos);
                        if (bestAirBubbleQuality == null || airQualityLevel.isBetterThan(bestAirBubbleQuality)) {
                            double distanceSq = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).distanceToSqr(location);
                            if (distanceSq < Math.pow(airQualityLevel.getAirProviderRadius(), 2.0)) {
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
        } else {
            return CommonConfig.getAirQualityAtLevelByDimension(level.dimension().location(), (int) Math.round(location.y));
        }
    }

    @Override
    public boolean isSensitiveToAirQuality(LivingEntity entity) {
        return entity.getType().is(ModRegistry.AIR_QUALITY_SENSITIVE_ENTITY_TYPE_TAG) && (!(entity instanceof Player player) || !player.getAbilities().invulnerable);
    }
}
