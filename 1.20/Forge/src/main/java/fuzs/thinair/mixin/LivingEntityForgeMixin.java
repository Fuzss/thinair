package fuzs.thinair.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class LivingEntityForgeMixin extends Entity {

    public LivingEntityForgeMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // We do the air level modification on our own, so ignore the game's inbuilt check
    // so here we pretend it can always breathe underwater
    @Redirect(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canDrownInFluidType(Lnet/minecraftforge/fluids/FluidType;)Z"))
    public boolean baseTick(LivingEntity self, FluidType fluidType) {
        return false;
    }
}
