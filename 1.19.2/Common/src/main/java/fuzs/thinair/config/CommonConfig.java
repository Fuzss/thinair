package fuzs.thinair.config;

import com.mojang.datafixers.util.Pair;
import fuzs.thinair.ThinAir;
import fuzs.thinair.helper.AirQualityLevel;
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
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> alwaysBreathingEntities;

    public static ForgeConfigSpec.BooleanValue enableSignalTorches;
    public static ForgeConfigSpec.IntValue drownedChoking;

    public static ForgeConfigSpec.DoubleValue soulFireRange;
    public static ForgeConfigSpec.DoubleValue soulCampfireRange;
    public static ForgeConfigSpec.DoubleValue soulTorchRange;
    public static ForgeConfigSpec.DoubleValue lavaRange;
    public static ForgeConfigSpec.DoubleValue netherPortalRange;

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

        alwaysBreathingEntities = builder
            .comment("Entities that can always breathe.",
                "This is in addition to entities where `.canBreatheUnderwater()` returns true (so, fish and undead),",
                "and invulnerable players.")
            .defineList("alwaysBreathingEntities", Arrays.asList(
                "minecraft:armor_stand",
                "minecraft:enderman",
                "minecraft:endermite",
                "minecraft:shulker",
                "minecraft:strider",
                "minecraft:ghast",
                "minecraft:piglin",
                "minecraft:hoglin",
                "minecraft:piglin_brute",
                "minecraft:blaze",
                "minecraft:magma_cube",
                "minecraft:ender_dragon",
                "minecraft:wither"
            ), o -> o instanceof String s && ResourceLocation.isValidResourceLocation(s));

        enableSignalTorches = builder
            .comment("Whether to allow right-clicking torches to make them spray particle effects")
            .define("enableSignalTorches", true);

        drownedChoking = builder
            .comment("How much air a Drowned attack removes")
            .defineInRange("drownedChoking", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);

        builder.push("Ranges");
        soulTorchRange = builder
            .comment("The radius a soul torch projects a bubble of blue air around it")
            .defineInRange("soulTorchRange", 2.0, 0.0, 32.0);
        soulFireRange = builder
            .comment("The radius a soul fire projects a bubble of blue air around it")
            .defineInRange("soulFireRange", 5.0, 0.0, 32.0);
        soulCampfireRange = builder
            .comment("The radius a soul campfire projects a bubble of blue air around it")
            .defineInRange("soulCampfireRange", 9.0, 0.0, 32.0);
        lavaRange = builder
            .comment("The radius lava blocks project a bubble of red air around it")
            .defineInRange("lavaRange", 5.0, 0.0, 32.0);
        netherPortalRange = builder
            .comment("The radius nether portal blocks project a bubble of green air around it")
            .defineInRange("netherPortalRange", 5.0, 0.0, 32.0);
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
