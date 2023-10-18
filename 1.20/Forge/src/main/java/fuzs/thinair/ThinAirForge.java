package fuzs.thinair;

import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.data.*;
import fuzs.thinair.init.ForgeModRegistry;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
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
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_PROTECTION_CAPABILITY, new CapabilityToken<AirProtectionCapability>() {});
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<AirBubblePositionsCapability>() {});
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final LivingBreatheEvent evt) -> {
            evt.setCanBreathe(false);
            evt.setConsumeAirAmount(0);
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingDrownEvent evt) -> {
            evt.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator dataGenerator = evt.getGenerator();
        dataGenerator.addProvider(evt.includeServer(), new ModAdvancementProvider(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModBlockLootProvider(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeClient(), new ModBlockModels(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModBlockTagsProvider(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModChestLootProvider(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModEntityTypeTagsProvider(evt, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModRecipeProvider(evt, ThinAir.MOD_ID));
    }
}
