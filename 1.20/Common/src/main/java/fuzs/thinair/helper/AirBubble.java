package fuzs.thinair.helper;

import fuzs.thinair.api.AirQualityLevel;

import java.util.Objects;

public record AirBubble(AirQualityLevel airQualityLevel, double radius) {

    public AirBubble {
        Objects.requireNonNull(airQualityLevel, "air quality level is null");
    }

    public AirBubble(AirQualityLevel airQualityLevel) {
        this(airQualityLevel, airQualityLevel.getAirProviderRadius());
    }
}
