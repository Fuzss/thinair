package fuzs.thinair.api;

import fuzs.thinair.ThinAir;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe
     */
    GREEN(true, true) {

        @Override
        boolean isProtected(LivingEntity entity) {
            return true;
        }

        @Override
        int getConsumedAirAmount(LivingEntity entity) {
            return 0;
        }
    },
    /**
     * No loss, no gain
     */
    BLUE(true, false) {

        @Override
        boolean isProtected(LivingEntity entity) {
            return true;
        }

        @Override
        int getConsumedAirAmount(LivingEntity entity) {
            return 0;
        }
    },
    /**
     * Slowly lose oxygen
     */
    YELLOW(false, false) {

        @Override
        boolean isProtected(LivingEntity entity) {
            for (ItemStack itemStack : entity.getAllSlots()) {
                if (itemStack.is(ModRegistry.BREATHING_UTILITY_ITEM_TAG)) {
                    if (itemStack.isDamageableItem()) {
                        if (!entity.level().isClientSide && entity.level().getGameTime() % (20 * 15) == 0) {
                            itemStack.hurt(1, entity.getRandom(), entity instanceof ServerPlayer player ? player : null);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        int getConsumedAirAmount(LivingEntity entity) {
            return entity.level().getGameTime() % 4 == 0 ? 1 : 0;
        }
    },
    /**
     * Completely unable to breathe (like underwater)
     */
    RED(false, false) {

        @Override
        boolean isProtected(LivingEntity entity) {
            return MobEffectUtil.hasWaterBreathing(entity) || ModRegistry.AIR_PROTECTION_CAPABILITY.maybeGet(entity).filter(AirProtectionCapability::isProtected).isPresent();
        }

        @Override
        int getConsumedAirAmount(LivingEntity entity) {
            return 1;
        }
    };

    public final boolean canBreathe;
    public final boolean canRefillAir;
    private final TagKey<Block> tag;

    AirQualityLevel(boolean canBreathe, boolean canRefillAir) {
        this.canBreathe = canBreathe;
        this.canRefillAir = canRefillAir;
        this.tag = TagKey.create(Registries.BLOCK, ThinAir.id(this.getSerializedName() + "_air_providers"));
    }

    public TagKey<Block> getAirProvidersTag() {
        return this.tag;
    }

    @Nullable
    public static AirQualityLevel getAirQualityFromBlock(BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.LIT) && !blockState.getValue(BlockStateProperties.LIT)) {
            return null;
        }
        for (AirQualityLevel airQualityLevel : AirQualityLevel.values()) {
            if (blockState.is(airQualityLevel.tag)) {
                return airQualityLevel;
            }
        }
        return null;
    }

    public double getAirProviderRadius() {
        return switch (this) {
            case RED -> CommonConfig.redAirProviderRadius.get();
            case GREEN -> CommonConfig.greenAirProviderRadius.get();
            case YELLOW -> CommonConfig.yellowAirProviderRadius.get();
            case BLUE -> CommonConfig.blueAirProviderRadius.get();
        };
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public int getLightLevel() {
        return 15 - this.ordinal() * 3;
    }

    public int getOutputSignal() {
        return this.ordinal() + 1;
    }

    public boolean isBetterThan(AirQualityLevel other) {
        return this.ordinal() < other.ordinal();
    }

    abstract boolean isProtected(LivingEntity entity);

    abstract int getConsumedAirAmount(LivingEntity entity);

    public int getConsumedAirAmountAfterProtection(LivingEntity entity) {
        AirQualityLevel[] values = AirQualityLevel.values();
        for (int i = values.length - 1; i >= this.ordinal(); i--) {
            if (values[i].isProtected(entity)) return 0;
        }
        return this.getConsumedAirAmount(entity);
    }
}
