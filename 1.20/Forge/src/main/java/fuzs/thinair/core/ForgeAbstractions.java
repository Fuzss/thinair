package fuzs.thinair.core;

import net.minecraft.world.entity.LivingEntity;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public boolean isEyeInAir(LivingEntity entity) {
        return entity.getEyeInFluidType().isAir();
    }
}
