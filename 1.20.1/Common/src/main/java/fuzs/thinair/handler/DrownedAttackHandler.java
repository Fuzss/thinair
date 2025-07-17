package fuzs.thinair.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.thinair.config.ServerConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedAttackHandler {

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (source.getEntity() instanceof Drowned && source.is(DamageTypes.MOB_ATTACK)) {
            entity.setAirSupply(entity.getAirSupply() - ServerConfig.drownedChoking.get());
        }
        return EventResult.PASS;
    }
}
