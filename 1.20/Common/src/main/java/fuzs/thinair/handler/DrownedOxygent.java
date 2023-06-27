package fuzs.thinair.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.thinair.config.CommonConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedOxygent {

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (source.getEntity() instanceof Drowned && !source.is(DamageTypeTags.IS_PROJECTILE)) {
            entity.setAirSupply(entity.getAirSupply() - CommonConfig.drownedChoking.get());
        }
        return EventResult.PASS;
    }
}
