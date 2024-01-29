package fuzs.thinair.api.v1;

import fuzs.thinair.ThinAir;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.core.CommonAbstractions;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe.
     */
    GREEN(true, true) {
        @Override
        boolean isProtected(LivingEntity entity) {
            return false;
        }

        @Override
        int getAirAmount(LivingEntity entity) {
            return 4;
        }
    },
    /**
     * No loss, no gain.
     */
    BLUE(true, false) {
        @Override
        boolean isProtected(LivingEntity entity) {
            return false;
        }

        @Override
        int getAirAmount(LivingEntity entity) {
            return 0;
        }
    },
    /**
     * Slowly loose oxygen.
     */
    YELLOW(false, false, "breathing_equipment") {
        @Override
        int getAirAmount(LivingEntity entity) {
            return entity.level().getGameTime() % 4 == 0 ? super.getAirAmount(entity) : 0;
        }
    },
    /**
     * Completely unable to breathe (like underwater).
     */
    RED(false, false, "heavy_breathing_equipment");

    public static final StringRepresentable.EnumCodec<AirQualityLevel> CODEC = StringRepresentable.fromEnum(
            AirQualityLevel::values);

    public final boolean canBreathe;
    public final boolean canRefillAir;
    @Nullable
    private final TagKey<Item> breathingEquipment;
    private final TagKey<Block> airProviders;

    AirQualityLevel(boolean canBreathe, boolean canRefillAir) {
        this(canBreathe, canRefillAir, null);
    }

    AirQualityLevel(boolean canBreathe, boolean canRefillAir, @Nullable String breathingEquipment) {
        this.canBreathe = canBreathe;
        this.canRefillAir = canRefillAir;
        this.breathingEquipment = breathingEquipment != null ? TagKey.create(Registries.ITEM,
                ThinAir.id(breathingEquipment)
        ) : null;
        this.airProviders = TagKey.create(Registries.BLOCK, ThinAir.id(this.getSerializedName() + "_air_providers"));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public TagKey<Item> getBreathingEquipment() {
        Objects.requireNonNull(this.breathingEquipment, "breathing equipment is null");
        return this.breathingEquipment;
    }

    public TagKey<Block> getAirProvidersTag() {
        return this.airProviders;
    }

    public double getAirProviderRadius() {
        return switch (this) {
            case RED -> CommonConfig.redAirProviderRadius.get();
            case GREEN -> CommonConfig.greenAirProviderRadius.get();
            case YELLOW -> CommonConfig.yellowAirProviderRadius.get();
            case BLUE -> CommonConfig.blueAirProviderRadius.get();
        };
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

    public int getAirAmountAfterProtection(LivingEntity entity) {
        return this.isProtected(entity) ? 0 : this.getAirAmount(entity);
    }

    boolean isProtected(LivingEntity entity) {
        return MobEffectUtil.hasWaterBreathing(entity) || entity.isUsingItem() && entity.getUseItem()
                .is(ModRegistry.AIR_REFILLER_ITEM_TAG) || this.isProtectedViaBreathingEquipment(entity);
    }

    int getAirAmount(LivingEntity entity) {
        return entity.getRandom().nextInt(EnchantmentHelper.getRespiration(entity) + 1) == 0 ? -1 : 0;
    }

    private boolean isProtectedViaBreathingEquipment(LivingEntity entity) {
        if (this.breathingEquipment != null) {
            ItemStack itemStack = CommonAbstractions.INSTANCE.findEquippedItem(entity, this.breathingEquipment);
            if (!itemStack.isEmpty() && entity.level().getGameTime() % (20 * 15) == 0) {
                itemStack.hurtAndBreak(1, entity, (LivingEntity livingEntity) -> {
                    livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                });
            }
            return !itemStack.isEmpty();
        } else {
            return false;
        }
    }

    public float getItemModelProperty() {
        return this.ordinal() / 10.0F;
    }

    @Nullable
    public static AirQualityLevel getAirQualityAtEyes(BlockState blockState) {
        if (blockState.is(Blocks.BUBBLE_COLUMN)) return GREEN;
        if (!blockState.getFluidState().isEmpty()) return RED;
        return getAirQualityFromBlock(blockState);
    }

    @Nullable
    public static AirQualityLevel getAirQualityFromBlock(BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.LIT) && !blockState.getValue(BlockStateProperties.LIT)) {
            return null;
        }
        for (AirQualityLevel airQualityLevel : AirQualityLevel.values()) {
            if (blockState.is(airQualityLevel.airProviders)) {
                return airQualityLevel;
            }
        }
        return null;
    }
}
