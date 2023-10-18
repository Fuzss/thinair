package fuzs.thinair.api;

import fuzs.thinair.helper.AirQualityHelperImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface AirQualityHelper {
    AirQualityHelper INSTANCE = new AirQualityHelperImpl();

    AirQualityLevel getAirQualityAtLocation(Level level, Vec3 location);

    boolean isSensitiveToAirQuality(LivingEntity entity);
}
