package fuzs.thinair.advancements.criterion;

import com.google.gson.JsonObject;
import fuzs.thinair.ThinAir;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SignalificateTorchTrigger extends SimpleCriterionTrigger<SignalificateTorchTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ThinAir.MOD_ID, "signalificate_torch");

    private static final String TAG_LOCATION = "location";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate,
        DeserializationContext ctx) {
        return new Instance(predicate, LocationPredicate.fromJson(json.get(TAG_LOCATION)));
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        super.trigger(player, inst -> inst.test(player.serverLevel(), pos));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        protected final LocationPredicate location;

        public Instance(ContextAwarePredicate predicate, LocationPredicate location) {
            super(ID, predicate);
            this.location = location;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            var json = super.serializeToJson(ctx);
            json.add(TAG_LOCATION, this.location.serializeToJson());
            return json;
        }

        private boolean test(ServerLevel level, BlockPos pos) {
            return this.location.matches(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }
}
