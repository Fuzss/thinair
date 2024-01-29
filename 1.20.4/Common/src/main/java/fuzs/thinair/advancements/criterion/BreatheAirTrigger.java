package fuzs.thinair.advancements.criterion;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.thinair.api.v1.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public class BreatheAirTrigger extends SimpleCriterionTrigger<BreatheAirTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AirQualityLevel airQualityLevel) {
        super.trigger(player, instance -> instance.matches(airQualityLevel));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<List<AirQualityLevel>> allowedQualities) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> {
                    return instance.group(
                            ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                            ExtraCodecs.strictOptionalField(AirQualityLevel.CODEC.listOf(), "air_qualities").forGetter(TriggerInstance::allowedQualities)
                    ).apply(instance, TriggerInstance::new);
        });

        public static Criterion<TriggerInstance> breatheAir(AirQualityLevel... airQualityLevels) {
            return ModRegistry.BREATHE_AIR_TRIGGER.value().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(ImmutableList.copyOf(airQualityLevels))));
        }

        public boolean matches(AirQualityLevel airQualityLevel) {
            return this.allowedQualities.isEmpty() || this.allowedQualities.get().contains(airQualityLevel);
        }
    }
}
