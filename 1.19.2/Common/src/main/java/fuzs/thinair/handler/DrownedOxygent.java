package fuzs.thinair.handler;

import fuzs.thinair.config.CommonConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedOxygent {

    public static void onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (source.getEntity() instanceof Drowned && !source.isProjectile()) {
            entity.setAirSupply(entity.getAirSupply() - CommonConfig.drownedChoking.get());
        }
    }
}
