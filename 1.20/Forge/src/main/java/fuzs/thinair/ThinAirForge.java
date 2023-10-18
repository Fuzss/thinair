package fuzs.thinair;

import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.api.AirQualityHelper;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.data.*;
import fuzs.thinair.init.ForgeModRegistry;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
        ModAdvancementTriggers.registerTriggers(CriteriaTriggers::register);
        registerCapabilities();
        registerHandlers();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<AirBubblePositionsCapability>() {});
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final LivingBreatheEvent evt) -> {

            LivingEntity entity = evt.getEntity();

            if (!AirQualityHelper.INSTANCE.isSensitiveToAirQuality(entity)) return;

            AirQualityLevel airQualityLevel = AirQualityHelper.INSTANCE.getAirQualityAtLocation(entity.level(), entity.getEyePosition());

            if (entity instanceof ServerPlayer player) {
                ModAdvancementTriggers.BREATHE_AIR.trigger(player, airQualityLevel);
            }

            evt.setCanBreathe(airQualityLevel.canBreathe);
            evt.setCanRefillAir(airQualityLevel.canRefillAir);
            if (!airQualityLevel.canBreathe && entity.getRandom().nextInt(EnchantmentHelper.getRespiration(entity) + 1) == 0) {
                evt.setConsumeAirAmount(airQualityLevel.getConsumedAirAmountAfterProtection(entity));
            }
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
