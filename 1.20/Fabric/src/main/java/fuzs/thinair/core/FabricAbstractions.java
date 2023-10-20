package fuzs.thinair.core;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public boolean isEyeInAir(LivingEntity entity) {
        return !entity.isEyeInFluid(FluidTags.WATER);
    }
}
