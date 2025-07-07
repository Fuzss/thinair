package fuzs.thinair.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import fuzs.thinair.ThinAir;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AirQualitySerializationHelper {
    static final Codec<Map.Entry<ResourceKey<Level>, DimensionAirQuality>> DIMENSION_ENTRY_CODEC = Codec.pair(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").codec(),
                    DimensionAirQuality.CODEC)
            .xmap((Pair<ResourceKey<Level>, DimensionAirQuality> pair) -> Map.entry(pair.getFirst(), pair.getSecond()),
                    (Map.Entry<ResourceKey<Level>, DimensionAirQuality> entry) -> Pair.of(entry.getKey(),
                            entry.getValue()));

    static List<String> encodeDimensionEntries(Map<ResourceKey<Level>, DimensionAirQuality> entries) {
        return entries.entrySet()
                .stream()
                .map((Map.Entry<ResourceKey<Level>, DimensionAirQuality> pair) -> DIMENSION_ENTRY_CODEC.encodeStart(
                        NbtOps.INSTANCE,
                        pair).getOrThrow(false, ThinAir.LOGGER::error))
                .map((Tag tag) -> {
                    return NbtUtils.toPrettyComponent(tag).getString();
                })
                .collect(Collectors.toList());
    }

    static Map<ResourceKey<Level>, DimensionAirQuality> parseDimensionEntries(List<? extends String> values) {
        return values.stream().<Optional<Map.Entry<ResourceKey<Level>, DimensionAirQuality>>>map(s -> {
            try {
                return DIMENSION_ENTRY_CODEC.parse(NbtOps.INSTANCE, TagParser.parseTag(s)).result();
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        }).filter(Optional::isPresent).map(Optional::get).collect(Util.toMap());
    }

    static boolean validateDimensionEntry(Object o) {
        try {
            return o instanceof String s && DIMENSION_ENTRY_CODEC.parse(NbtOps.INSTANCE, TagParser.parseTag(s))
                    .error()
                    .isEmpty();
        } catch (CommandSyntaxException e) {
            return false;
        }
    }
}
