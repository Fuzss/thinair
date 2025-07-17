package fuzs.thinair.config;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.thinair.api.v1.AirQualityLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServerConfig implements ConfigCore {
    static final Map<ResourceKey<Level>, DimensionAirQuality> DEFAULT_DIMENSION_ENTRIES = ImmutableMap.of(Level.OVERWORLD,
            new DimensionAirQuality(AirQualityLevel.YELLOW,
                    new DimensionAirQuality.BoundedAirQuality(AirQualityLevel.GREEN, 0, 256)),
            Level.NETHER,
            new DimensionAirQuality(AirQualityLevel.YELLOW),
            Level.END,
            new DimensionAirQuality(AirQualityLevel.RED));

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensions;

    public static ForgeConfigSpec.BooleanValue enableSignalTorches;
    public static ForgeConfigSpec.IntValue drownedChoking;

    public static ForgeConfigSpec.DoubleValue blueAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue redAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue yellowAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue greenAirProviderRadius;

    private static Map<ResourceKey<Level>, DimensionAirQuality> dimensionEntries = Collections.emptyMap();

    @Override
    public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        dimensions = builder.comment(
                        "Air qualities at different heights in different dimensions, with one string applying to one dimension.",
                        "A basic entry consists of the dimension's identifier and the default air level in that dimension.",
                        "Furthermore the quality at different heights can be overridden by defining a height range and a corresponding quality.",
                        "If a dimension doesn't have an entry here, it'll be assumed to be green everywhere.",
                        "The string entries use SNBT format, like vanilla commands do.")
                .defineList("dimensions",
                        AirQualitySerializationHelper.encodeDimensionEntries(DEFAULT_DIMENSION_ENTRIES),
                        AirQualitySerializationHelper::validateDimensionEntry);

        enableSignalTorches = builder.comment(
                        "Whether to allow right-clicking torches to make them spray particle effects")
                .define("enableSignalTorches", true);

        drownedChoking = builder.comment("How much air a Drowned attack removes. Set to 0 to disable this feature.")
                .defineInRange("drownedChoking", 100, 0, 72000);

        builder.push("Ranges");
        yellowAirProviderRadius = builder.comment(
                        "The radius in which all blocks defined in the yellow air providers tag project a bubble of air around them.")
                .defineInRange("yellowAirProviderRadius", 6.0, 1.0, 32.0);
        blueAirProviderRadius = builder.comment(
                        "The radius in which all blocks defined in the blue air providers tag (usually soul fire related blocks) project a bubble of air around them.")
                .defineInRange("blueAirProviderRadius", 6.0, 1.0, 32.0);
        redAirProviderRadius = builder.comment(
                        "The radius in which all blocks defined in the red air providers tag (usually lava related blocks) project a bubble of air around them.")
                .defineInRange("redAirProviderRadius", 3.0, 1.0, 32.0);
        greenAirProviderRadius = builder.comment(
                        "The radius in which all blocks defined in the green air providers tag (usually various portal blocks) project a bubble of air around them.")
                .defineInRange("greenAirProviderRadius", 9.0, 1.0, 32.0);
        builder.pop();
    }

    @Override
    public void afterConfigReload() {
        dimensionEntries = AirQualitySerializationHelper.parseDimensionEntries(dimensions.get());
    }

    public static AirQualityLevel getAirQualityAtLevelByDimension(ResourceKey<Level> dimension, int height) {
        return dimensionEntries.getOrDefault(dimension, DimensionAirQuality.DEFAULT).getAirQualityAtHeight(height);
    }
}
