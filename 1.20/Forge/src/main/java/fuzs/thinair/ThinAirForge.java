package fuzs.thinair;

import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.data.*;
import fuzs.thinair.handler.TickAirHandler;
import fuzs.thinair.init.ForgeModRegistry;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.network.ClientboundChunkAirQualityMessage;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.OptionalInt;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new, ContentRegistrationFlags.COPY_TAG_RECIPES);
        registerCapabilities();
        registerHandlers();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<AirBubblePositionsCapability>() {});
    }

    @Deprecated(forRemoval = true)
    private static void registerHandlers() {
        // TODO remove this and switch to puzzles implementation
        MinecraftForge.EVENT_BUS.addListener((final LivingBreatheEvent evt) -> {
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
            boolean canLoseAir = !entity.canDrownInFluidType(entity.getEyeInFluidType()) && !MobEffectUtil.hasWaterBreathing(entity) && (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable);
            EventResult result = TickAirHandler.onLivingBreathe(entity, airAmount, evt.canRefillAir(), canLoseAir);
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
        MinecraftForge.EVENT_BUS.addListener((final ChunkWatchEvent.Watch evt) -> {
            ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(evt.getChunk()).ifPresent(capability -> {
                if (!capability.getAirBubblePositions().isEmpty()) {
                    ThinAir.NETWORK.sendTo(evt.getPlayer(), new ClientboundChunkAirQualityMessage(evt.getPos(), capability.toCompoundTag()));
                }
            });
        });
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(true, new ModAdvancementProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModBlockLootProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(evt.includeClient(), new ModModelProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModBlockTagsProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModChestLootProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModEntityTypeTagsProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModItemTagsProvider(evt, ThinAir.MOD_ID));
        evt.getGenerator().addProvider(true, new ModRecipeProvider(evt, ThinAir.MOD_ID));
    }
}
