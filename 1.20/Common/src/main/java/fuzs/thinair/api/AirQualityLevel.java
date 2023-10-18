package fuzs.thinair.api;

import fuzs.thinair.ThinAir;
import fuzs.thinair.config.CommonConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe
     */
    GREEN(),
    /**
     * No loss, no gain
     */
    BLUE(),
    /**
     * Slowly lose oxygen
     */
    YELLOW(),
    /**
     * Completely unable to breathe (like underwater)
     */
    RED();

    private final TagKey<Block> tag;

    AirQualityLevel() {
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
}
