package fuzs.thinair.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class SignalifyTorchTrigger extends SimpleCriterionTrigger<SignalifyTorchTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        super.trigger(player, instance -> instance.matches(player.serverLevel(), pos));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<LocationPredicate> location) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                                ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(TriggerInstance::location)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> signalifyTorch(LocationPredicate.Builder location) {
            return ModRegistry.SIGNALIFY_TORCH_TRIGGER.value().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(location.build())));
        }

        public boolean matches(ServerLevel level, BlockPos pos) {
            return this.location.isEmpty() || this.location.get().matches(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }
}
