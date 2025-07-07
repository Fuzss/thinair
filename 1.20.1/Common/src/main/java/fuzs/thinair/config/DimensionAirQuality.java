package fuzs.thinair.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.thinair.api.v1.AirQualityLevel;

import java.util.Collections;
import java.util.List;

record DimensionAirQuality(AirQualityLevel baseQuality, List<BoundedAirQuality> boundedQualities) {
    public static final Codec<DimensionAirQuality> CODEC = RecordCodecBuilder.<DimensionAirQuality>create(instance -> instance.group(
            AirQualityLevel.CODEC.optionalFieldOf("base_quality", AirQualityLevel.GREEN)
                    .forGetter(DimensionAirQuality::baseQuality),
            BoundedAirQuality.CODEC.listOf()
                    .optionalFieldOf("bounded_qualities", Collections.emptyList())
                    .forGetter(DimensionAirQuality::boundedQualities)).apply(instance, DimensionAirQuality::new));
    public static final DimensionAirQuality DEFAULT = new DimensionAirQuality(AirQualityLevel.GREEN,
            Collections.emptyList());

    DimensionAirQuality(AirQualityLevel quality, BoundedAirQuality... bounds) {
        this(quality, List.of(bounds));
    }

    public AirQualityLevel getAirQualityAtHeight(int height) {
        for (BoundedAirQuality bound : this.boundedQualities) {
            if (bound.containsValue(height)) {
                return bound.quality();
            }
        }

        return this.baseQuality;
    }

    record BoundedAirQuality(AirQualityLevel quality, int min, int max) {
        public static final Codec<BoundedAirQuality> CODEC = RecordCodecBuilder.<BoundedAirQuality>create(instance -> instance.group(
                                AirQualityLevel.CODEC.fieldOf("quality").forGetter(BoundedAirQuality::quality),
                                Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(BoundedAirQuality::min),
                                Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(BoundedAirQuality::max))
                        .apply(instance, BoundedAirQuality::new))
                .flatXmap(BoundedAirQuality::validate, BoundedAirQuality::validate);

        private static DataResult<BoundedAirQuality> validate(BoundedAirQuality value) {
            return value.max <= value.min ?
                    DataResult.error(() -> "Max must be larger than min, min: " + value.min + ", max: " + value.max) :
                    DataResult.success(value);
        }

        public boolean containsValue(int value) {
            return value >= this.min && value < this.max;
        }
    }
}
