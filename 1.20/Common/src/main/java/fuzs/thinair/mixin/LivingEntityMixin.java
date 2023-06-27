package fuzs.thinair.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // We also regenerate air on our own, so ignore the game's inbuilt check...
    // The game checks if `getAirSupply` is less than `getMaxAirSupply`, so we mixin
    // to make `getMaxAirSupply` always return negative a gazillion instead so it
    // never restores any air.
    @Redirect(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMaxAirSupply()I"))
    public int getAirSupplyProxy(LivingEntity e) {
        return Integer.MIN_VALUE;
    }
}
