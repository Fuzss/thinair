package fuzs.thinair.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.AirQualityLevel;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumSet;
import java.util.Locale;

public class BreatheAirTrigger extends SimpleCriterionTrigger<BreatheAirTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ThinAir.MOD_ID, "breathe_bad_air");

    private static final String TAG_AIR_QUALITIES = "AirQualities";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext ctx) {

        var allowedQualities = EnumSet.noneOf(AirQualityLevel.class);
        for (var qualObj : json.getAsJsonArray(TAG_AIR_QUALITIES)) {
            var qualStr = qualObj.getAsString();
            allowedQualities.add(AirQualityLevel.valueOf(qualStr.toUpperCase(Locale.ROOT)));
        }
        return new Instance(predicate, allowedQualities);
    }

    public void trigger(ServerPlayer player, AirQualityLevel qualityBreathed) {
        super.trigger(player, inst -> inst.test(qualityBreathed));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        protected final EnumSet<AirQualityLevel> allowedQualities;

        public Instance(ContextAwarePredicate predicate, EnumSet<AirQualityLevel> allowedQualities) {
            super(ID, predicate);
            this.allowedQualities = allowedQualities;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            var json = super.serializeToJson(ctx);

            var qualities = new JsonArray();
            for (var qual : this.allowedQualities) {
                qualities.add(qual.getSerializedName());
            }
            json.add(TAG_AIR_QUALITIES, qualities);
            return json;
        }

        private boolean test(AirQualityLevel qualityBreathed) {
            return this.allowedQualities.contains(qualityBreathed);
        }
    }
}
