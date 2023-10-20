package fuzs.thinair.core;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.thinair.handler.TickAirHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalInt;

@Deprecated(forRemoval = true)
public final class FabricEventImplHelper {

    private FabricEventImplHelper() {

    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean tryDrown) {
        boolean canLoseAir = !entity.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(entity) && (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable);
        tickAirSupply(entity, originalAirSupply, canRefillAir, canLoseAir, tryDrown);
    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean canLoseAir, boolean tryDrown) {
        DefaultedInt airAmount = DefaultedInt.fromValue(entity.getAirSupply() - originalAirSupply);
        EventResult result = TickAirHandler.onLivingBreathe(entity, airAmount, canRefillAir, canLoseAir);
        if (result.isInterrupt()) {
            entity.setAirSupply(originalAirSupply);
        } else {
            OptionalInt optional = airAmount.getAsOptionalInt();
            if (optional.isPresent()) {
                entity.setAirSupply(Math.min(originalAirSupply + optional.getAsInt(), entity.getMaxAirSupply()));
            }
        }
        if (tryDrown) tryDrownEntity(entity);
    }

    private static void tryDrownEntity(LivingEntity entity) {
        if (entity.getAirSupply() > 0) return;
        boolean isDrowning = entity.getAirSupply() <= -20;
        EventResult result = EventResult.PASS;
        if (result.isInterrupt()) isDrowning = result.getAsBoolean();
        if (isDrowning) {
            entity.setAirSupply(0);
            Vec3 deltaMovement = entity.getDeltaMovement();
            for (int i = 0; i < 8; ++i) {
                double offsetX = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetY = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetZ = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                entity.level().addParticle(ParticleTypes.BUBBLE, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, deltaMovement.x, deltaMovement.y, deltaMovement.z);
            }
            entity.hurt(entity.damageSources().drown(), 2.0F);
        }
    }
}
