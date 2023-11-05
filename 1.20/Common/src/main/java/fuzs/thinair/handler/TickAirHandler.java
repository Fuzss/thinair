package fuzs.thinair.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.api.v1.AirQualityHelper;
import fuzs.thinair.api.v1.AirQualityLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class TickAirHandler {

    public static EventResult onLivingBreathe(LivingEntity entity, DefaultedInt airAmount, boolean canRefillAir, boolean canLoseAir) {

        if (!AirQualityHelper.INSTANCE.isSensitiveToAirQuality(entity)) return EventResult.PASS;

        AirQualityLevel airQualityLevel = AirQualityHelper.INSTANCE.getAirQualityAtLocation(entity);

        if (entity instanceof ServerPlayer player) {
            ModAdvancementTriggers.BREATHE_AIR.trigger(player, airQualityLevel);
        }

        airAmount.accept(airQualityLevel.getAirAmountAfterProtection(entity));

        return EventResult.PASS;
    }
}
