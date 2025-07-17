package fuzs.thinair;

import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.ForgeEventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingEvents;
import fuzs.thinair.data.*;
import fuzs.thinair.init.ForgeModRegistry;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.integration.curios.CuriosIntegration;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.OptionalInt;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
        registerCapabilities();
        // TODO remove when this has been re-enabled in Puzzles Lib
        registerEventInvokers();
        registerIntegrations();
        DataProviderHelper.registerDataProviders(ThinAir.MOD_ID,
                ModAdvancementProvider::new,
                ModBlockLootProvider::new,
                ModModelProvider::new,
                ModBlockTagsProvider::new,
                ModChestLootProvider::new,
                ModEntityTypeTagsProvider::new,
                ModRecipeProvider::new);
        DataProviderHelper.registerDataProviders(ThinAir.MOD_ID, ModItemTagsProvider::new);
    }

    @Deprecated
    private static void registerEventInvokers() {
        ForgeEventInvokerRegistry.INSTANCE.register(LivingEvents.Breathe.class,
                LivingBreatheEvent.class,
                (LivingEvents.Breathe callback, LivingBreatheEvent evt) -> {
                    final int airAmountValue;
                    if (!evt.canBreathe()) {
                        airAmountValue = -evt.getConsumeAirAmount();
                    } else if (evt.canRefillAir()) {
                        airAmountValue = evt.getRefillAirAmount();
                    } else {
                        airAmountValue = 0;
                    }
                    DefaultedInt airAmount = DefaultedInt.fromValue(airAmountValue);
                    LivingEntity entity = evt.getEntity();
                    // do not use LivingBreatheEvent::canBreathe, it is merged with LivingBreatheEvent::canRefillAir, so recalculate the value
                    boolean canLoseAir =
                            !entity.canDrownInFluidType(entity.getEyeInFluidType()) && !MobEffectUtil.hasWaterBreathing(
                                    entity) && (!(entity instanceof Player)
                                    || !((Player) entity).getAbilities().invulnerable);
                    EventResult result = callback.onLivingBreathe(entity, airAmount, evt.canRefillAir(), canLoseAir);
                    if (result.isInterrupt()) {
                        evt.setCanBreathe(true);
                        evt.setCanRefillAir(false);
                    } else {
                        OptionalInt optional = airAmount.getAsOptionalInt();
                        if (optional.isPresent()) {
                            if (optional.getAsInt() < 0) {
                                evt.setCanBreathe(false);
                                evt.setConsumeAirAmount(Math.abs(optional.getAsInt()));
                            } else {
                                evt.setCanBreathe(true);
                                evt.setCanRefillAir(true);
                                evt.setRefillAirAmount(optional.getAsInt());
                            }
                        }
                    }
                });
        ForgeEventInvokerRegistry.INSTANCE.register(LivingEvents.Drown.class,
                LivingDrownEvent.class,
                (LivingEvents.Drown callback, LivingDrownEvent evt) -> {
                    EventResult result = callback.onLivingDrown(evt.getEntity(),
                            evt.getEntity().getAirSupply(),
                            evt.isDrowning());
                    if (result.isInterrupt()) {
                        if (result.getAsBoolean()) {
                            evt.setDrowning(true);
                        } else {
                            evt.setCanceled(true);
                        }
                    }
                });
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<>() {
            // NO-OP
        });
    }

    private static void registerIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            CuriosIntegration.registerHandlers();
        }
    }
}
