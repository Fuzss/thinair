package fuzs.thinair.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.impl.event.FabricEventImplHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Unique
    private int puzzleslib$originalAirSupply;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z", shift = At.Shift.BEFORE))
    public void baseTick$0(CallbackInfo callback) {
        this.puzzleslib$originalAirSupply = this.getAirSupply();
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V", ordinal = 0, shift = At.Shift.AFTER), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;decreaseAirSupply(I)I")))
    public void baseTick$1(CallbackInfo callback) {
        if (this.puzzleslib$originalAirSupply != Integer.MIN_VALUE) {
            FabricEventImplHelper.tickAirSupply(LivingEntity.class.cast(this), this.puzzleslib$originalAirSupply, false, true, false);
            this.puzzleslib$originalAirSupply = Integer.MIN_VALUE;
        }
    }

    @ModifyVariable(method = "baseTick", at = @At("LOAD"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAbilities()Lnet/minecraft/world/entity/player/Abilities;")))
    public boolean baseTick$2(boolean canLoseAir) {
        if (!canLoseAir) {
            if (this.puzzleslib$originalAirSupply != Integer.MIN_VALUE) {
                FabricEventImplHelper.tickAirSupply(LivingEntity.class.cast(this), this.puzzleslib$originalAirSupply, false, false, true);
                this.puzzleslib$originalAirSupply = Integer.MIN_VALUE;
            }
        }
        return canLoseAir;
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V", ordinal = 0, shift = At.Shift.AFTER), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I")))
    public void baseTick$3(CallbackInfo callback) {
        if (this.puzzleslib$originalAirSupply != Integer.MIN_VALUE) {
            FabricEventImplHelper.tickAirSupply(LivingEntity.class.cast(this), this.puzzleslib$originalAirSupply, true, true);
            this.puzzleslib$originalAirSupply = Integer.MIN_VALUE;
        }
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I")))
    public void baseTick$4(CallbackInfo callback) {
        if (this.puzzleslib$originalAirSupply != Integer.MIN_VALUE) {
            FabricEventImplHelper.tickAirSupply(LivingEntity.class.cast(this), this.puzzleslib$originalAirSupply, true, true);
            this.puzzleslib$originalAirSupply = Integer.MIN_VALUE;
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAirSupply()I", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;decreaseAirSupply(I)I")))
    public void baseTick$5(CallbackInfo callback) {
        if (this.getAirSupply() > 0) return;
        EventResult result = FabricLivingEvents.LIVING_DROWN.invoker().onLivingDrown(LivingEntity.class.cast(this), this.getAirSupply(), this.getAirSupply() <= -20);
        if (result.isInterrupt()) {
            this.puzzleslib$originalAirSupply = this.getAirSupply();
            if (result.getAsBoolean()) {
                this.setAirSupply(-20);
            } else {
                this.setAirSupply(Integer.MAX_VALUE);
            }
        }
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;drown()Lnet/minecraft/world/damagesource/DamageSource;")))
    public void baseTick$6(CallbackInfo callback) {
        if (this.puzzleslib$originalAirSupply != Integer.MIN_VALUE) {
            this.setAirSupply(this.puzzleslib$originalAirSupply);
            this.puzzleslib$originalAirSupply = Integer.MIN_VALUE;
        }
    }
}
