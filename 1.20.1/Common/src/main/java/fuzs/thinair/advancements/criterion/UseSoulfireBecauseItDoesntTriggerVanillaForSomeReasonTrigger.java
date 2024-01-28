package fuzs.thinair.advancements.criterion;

import com.google.gson.JsonObject;
import fuzs.thinair.ThinAir;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger
    extends SimpleCriterionTrigger<UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ThinAir.MOD_ID, "use_soulfire_bottle");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject pJson, ContextAwarePredicate pPlayer,
        DeserializationContext pContext) {
        return new Instance(pPlayer);
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, inst -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate pPlayer) {
            super(ID, pPlayer);
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }
    }
}
