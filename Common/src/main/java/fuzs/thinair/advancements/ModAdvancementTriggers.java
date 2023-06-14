package fuzs.thinair.advancements;

import fuzs.thinair.advancements.criterion.BreatheAirTrigger;
import fuzs.thinair.advancements.criterion.SignalificateTorchTrigger;
import fuzs.thinair.advancements.criterion.UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger;
import net.minecraft.advancements.CriterionTrigger;

import java.util.function.Consumer;

public class ModAdvancementTriggers {
    public static final BreatheAirTrigger BREATHE_AIR = new BreatheAirTrigger();
    public static final SignalificateTorchTrigger SIGNALIFICATE_TORCH = new SignalificateTorchTrigger();
    public static final UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger
        USE_SOULFIRE_BOTTLE = new UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger();

    public static void registerTriggers(Consumer<CriterionTrigger<?>> consumer) {
        consumer.accept(BREATHE_AIR);
        consumer.accept(SIGNALIFICATE_TORCH);
        consumer.accept(USE_SOULFIRE_BOTTLE);
    }
}
