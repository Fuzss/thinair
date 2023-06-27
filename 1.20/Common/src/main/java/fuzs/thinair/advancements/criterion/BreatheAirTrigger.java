package fuzs.thinair.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import fuzs.thinair.ThinAir;
import fuzs.thinair.advancements.AirProtectionSource;
import fuzs.thinair.advancements.AirSource;
import fuzs.thinair.helper.AirQualityLevel;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Locale;

public class BreatheAirTrigger extends SimpleCriterionTrigger<BreatheAirTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ThinAir.MOD_ID, "breathe_bad_air");

    private static final String TAG_AIR_QUALITIES = "air_qualities";
    private static final String TAG_PROTECTION = "protection";
    private static final String TAG_AIR_SOURCE = "air_source";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate,
        DeserializationContext ctx) {

        var allowedQualities = EnumSet.noneOf(AirQualityLevel.class);
        for (var qualObj : json.getAsJsonArray(TAG_AIR_QUALITIES)) {
            var qualStr = qualObj.getAsString();
            allowedQualities.add(AirQualityLevel.valueOf(qualStr.toUpperCase(Locale.ROOT)));
        }
        Either<AirProtectionSource, Unit> protection = null;
        if (json.has(TAG_PROTECTION) && !json.get(TAG_PROTECTION).isJsonNull()) {
            var protectionStr = GsonHelper.getAsString(json, TAG_PROTECTION).toUpperCase(Locale.ROOT);
            if (protectionStr.equals("ANY")) {
                protection = Either.right(Unit.INSTANCE);
            } else {
                protection = Either.left(AirProtectionSource.valueOf(protectionStr.toUpperCase(Locale.ROOT)));
            }
        }
        AirSource source = null;
        if (json.has(TAG_AIR_SOURCE) && !json.get(TAG_AIR_SOURCE).isJsonNull()) {
            var sourceStr = GsonHelper.getAsString(json, TAG_AIR_SOURCE);
            source = AirSource.valueOf(sourceStr.toUpperCase(Locale.ROOT));
        }
        return new Instance(predicate, allowedQualities, source, protection);
    }

    public void trigger(ServerPlayer player, AirQualityLevel qualityBreathed, AirSource source,
        AirProtectionSource protection) {
        super.trigger(player, inst -> inst.test(qualityBreathed, source, protection));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        protected final EnumSet<AirQualityLevel> allowedQualities;
        // Null = I don't care
        @Nullable
        protected final AirSource airSource;
        // APS = this specific source
        // Unit = any existent source (serialized as "any")
        // Null = don't care
        @Nullable
        protected final Either<AirProtectionSource, Unit> protection;

        public Instance(ContextAwarePredicate predicate, EnumSet<AirQualityLevel> allowedQualities,
            @Nullable AirSource airSource, @Nullable Either<AirProtectionSource, Unit> protection) {
            super(ID, predicate);
            this.allowedQualities = allowedQualities;
            this.airSource = airSource;
            this.protection = protection;
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
            if (this.airSource != null) {
                json.addProperty(TAG_AIR_SOURCE, this.airSource.toString().toLowerCase(Locale.ROOT));
            } else {
                json.add(TAG_AIR_SOURCE, JsonNull.INSTANCE);
            }
            if (this.protection != null) {
                var str = this.protection.map(
                    aps -> aps.toString().toLowerCase(Locale.ROOT),
                    unit -> "any");
                json.addProperty(TAG_PROTECTION, str);
            } else {
                json.add(TAG_PROTECTION, JsonNull.INSTANCE);
            }
            return json;
        }

        private boolean test(AirQualityLevel qualityBreathed, AirSource source, AirProtectionSource protection) {
            return this.allowedQualities.contains(qualityBreathed)
                && (this.airSource == null || this.airSource == source)
                && (this.protection == null || this.protection.map(aps -> aps == protection,
                unit -> protection != AirProtectionSource.NONE));
        }
    }
}
