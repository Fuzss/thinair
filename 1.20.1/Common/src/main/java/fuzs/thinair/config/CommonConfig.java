package fuzs.thinair.config;

import com.mojang.datafixers.util.Pair;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.v1.AirQualityLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommonConfig {
    public static final ForgeConfigSpec SPEC;

    static {
        var specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        SPEC = specPair.getRight();
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensions;

    public static ForgeConfigSpec.BooleanValue enableSignalTorches;
    public static ForgeConfigSpec.IntValue drownedChoking;

    public static ForgeConfigSpec.DoubleValue blueAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue redAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue yellowAirProviderRadius;
    public static ForgeConfigSpec.DoubleValue greenAirProviderRadius;

    private static Map<ResourceLocation, DimensionEntry> dimensionEntries = null;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        dimensions = builder
            .comment("Air qualities at different heights in different dimensions.",
                "The syntax is the dimension's resource location, the \"default\" air level in that dimension,",
                "then any number of height:airlevel pairs separated by commas.",
                "The air will have that quality starting at that height and above (until the next entry).",
                "The entries must be in ascending order of height.",
                "If a dimension doesn't have an entry here, it'll be assumed to be green everywhere.")
            .defineList("dimensions", Arrays.asList(
                "minecraft:overworld=yellow,0:green,128:yellow",
                "minecraft:the_nether=yellow",
                "minecraft:the_end=red"
            ), o -> o instanceof String s && parseDimensionLine(s) != null);

        enableSignalTorches = builder
            .comment("Whether to allow right-clicking torches to make them spray particle effects")
            .define("enableSignalTorches", true);

        drownedChoking = builder
            .comment("How much air a Drowned attack removes. Set to 0 to disable this feature.")
            .defineInRange("drownedChoking", 100, 0, 72000);

        builder.push("Ranges");
        yellowAirProviderRadius = builder
                .comment("The radius in which all blocks defined in the yellow air providers tag project a bubble of air around them.")
            .defineInRange("yellowAirProviderRadius", 6.0, 1.0, 32.0);
        blueAirProviderRadius = builder
            .comment("The radius in which all blocks defined in the blue air providers tag (usually soul fire related blocks) project a bubble of air around them.")
            .defineInRange("blueAirProviderRadius", 6.0, 1.0, 32.0);
        redAirProviderRadius = builder
                .comment("The radius in which all blocks defined in the red air providers tag (usually lava related blocks) project a bubble of air around them.")
            .defineInRange("redAirProviderRadius", 3.0, 1.0, 32.0);
        greenAirProviderRadius = builder
                .comment("The radius in which all blocks defined in the green air providers tag (usually various portal blocks) project a bubble of air around them.")
            .defineInRange("greenAirProviderRadius", 9.0, 1.0, 32.0);
        builder.pop();
    }

    @Nullable
    private static Pair<ResourceLocation, DimensionEntry> parseDimensionLine(String line) {
        var dimensionVals = line.split("=");
        if (dimensionVals.length != 2) {
            ThinAir.LOGGER.warn("Couldn't parse dimension line {}: couldn't split across `=` into 2 parts", line);
            return null;
        }

        var dimkey = ResourceLocation.tryParse(dimensionVals[0]);
        if (dimkey == null) {
            ThinAir.LOGGER.warn("Couldn't parse dimension line {}: {} isn't a valid resource location", line,
                dimensionVals[0]);
            return null;
        }

        var heightAndRest = dimensionVals[1].split(",", 2);
        AirQualityLevel baseQuality;
        try {
            baseQuality = AirQualityLevel.valueOf(heightAndRest[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            ThinAir.LOGGER.warn("Couldn't parse dimension line {}: {} isn't a valid base air quality", line,
                heightAndRest[0]);
            return null;
        }

        var heights = new ArrayList<Pair<Integer, AirQualityLevel>>();
        if (heightAndRest.length == 2) {
            var heightPairStrs = heightAndRest[1].split(",");
            Integer prevHeight = null;
            for (var heightPairStr : heightPairStrs) {
                var pairStr = heightPairStr.split(":");
                if (pairStr.length != 2) {
                    ThinAir.LOGGER.warn("Couldn't parse dimension line {}: couldn't use {} as a height entry",
                        line,
                        heightPairStr);
                    return null;
                }

                int height;
                try {
                    height = Integer.parseInt(pairStr[0]);
                } catch (NumberFormatException e) {
                    ThinAir.LOGGER.warn("Couldn't parse dimension line {}: {} isn't a valid int", line,
                        pairStr[0]);
                    return null;
                }
                if (prevHeight != null && height <= prevHeight) {
                    return null;
                }
                prevHeight = height;

                AirQualityLevel quality;
                try {
                    quality = AirQualityLevel.valueOf(pairStr[1].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    ThinAir.LOGGER.warn("Couldn't parse dimension line {}: {} isn't a valid air quality", line,
                        pairStr[1]);
                    return null;
                }

                heights.add(new Pair<>(height, quality));
            }
        }

        return new Pair<>(dimkey, new DimensionEntry(baseQuality, heights));
    }

    private record DimensionEntry(AirQualityLevel baseQuality, List<Pair<Integer, AirQualityLevel>> heights) {

    }

    public static AirQualityLevel getAirQualityAtLevelByDimension(ResourceLocation dimension, int y) {
        if (dimensionEntries == null) {
            // parse!
            var lines = dimensions.get();
            dimensionEntries = new HashMap<>(lines.size());
            for (var line : dimensions.get()) {
                var entry = parseDimensionLine(line);
                if (entry == null) {
                    ThinAir.LOGGER.warn("Somehow managed to get a bad dimension config past the validator?!");
                    continue;
                }
                dimensionEntries.put(entry.getFirst(), entry.getSecond());
            }
        }

        if (dimensionEntries.containsKey(dimension)) {
            var entry = dimensionEntries.get(dimension);
            List<Pair<Integer, AirQualityLevel>> heights = entry.heights;
            for (int i = 0; i < heights.size(); i++) {
                Pair<Integer, AirQualityLevel> heightPair = heights.get(heights.size() - i - 1);
                if (y >= heightPair.getFirst()) {
                    return heightPair.getSecond();
                }
            }
            return entry.baseQuality;
        } else {
            return AirQualityLevel.GREEN;
        }
    }
}
