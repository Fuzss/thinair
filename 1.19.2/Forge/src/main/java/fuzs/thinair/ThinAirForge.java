package fuzs.thinair;

import fuzs.puzzleslib.capability.ForgeCapabilityController;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.capability.AirBubblePositionsCapability;
import fuzs.thinair.capability.AirProtectionCapability;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.data.*;
import fuzs.thinair.handler.AirBubbleTracker;
import fuzs.thinair.handler.DrownedOxygent;
import fuzs.thinair.handler.TickAirChecker;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.integration.curios.CuriosIntegration;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.Optional;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        CommonFactories.INSTANCE.modConstructor(ThinAir.MOD_ID).accept(new ThinAir());
        ModAdvancementTriggers.registerTriggers(CriteriaTriggers::register);
        registerCapabilities();
        registerHandlers();
    }

    private static void registerCapabilities() {
        ForgeCapabilityController.setCapabilityToken(ModRegistry.AIR_PROTECTION_CAPABILITY, new CapabilityToken<AirProtectionCapability>() {});
        ForgeCapabilityController.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<AirBubblePositionsCapability>() {});
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.RightClickBlock evt) -> {
            Optional<Unit> result = SignalTorchBlock.switchTorchType(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec());
            result.ifPresent(unit -> evt.setUseItem(Event.Result.DENY));
        });
        MinecraftForge.EVENT_BUS.addListener((final ChunkEvent.Load evt) -> {
            if (evt.getLevel().isClientSide()) return;
            AirBubbleTracker.onChunkLoadServer(evt.getLevel(), evt.getChunk());
        });
        MinecraftForge.EVENT_BUS.addListener((final LevelEvent.Unload evt) -> {
            if (evt.getLevel().isClientSide()) return;
            AirBubbleTracker.onWorldClose(Proxy.INSTANCE.getGameServer(), evt.getLevel());
        });
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.LevelTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END || evt.level.isClientSide) return;
            AirBubbleTracker.consumeReqdChunksServer(evt.level);
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingHurtEvent evt) -> {
            DrownedOxygent.onLivingHurt(evt.getEntity(), evt.getSource(), evt.getAmount());
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingEvent.LivingTickEvent evt) -> {
            TickAirChecker.onLivingTick(evt.getEntity());
        });
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            CuriosIntegration.registerHandlers();
        }
    }

    @SubscribeEvent
    public static void onInterModEnqueue(final InterModEnqueueEvent evt) {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            CuriosIntegration.sendInterModComms();
        }
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator dataGenerator = evt.getGenerator();
        ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(evt.includeServer(), new ModAdvancementProvider(dataGenerator, fileHelper));
        dataGenerator.addProvider(evt.includeClient(), new ModBlockModels(dataGenerator, ThinAir.MOD_ID, fileHelper));
        dataGenerator.addProvider(evt.includeServer(), new ModBlockTagsProvider(dataGenerator, fileHelper));
        dataGenerator.addProvider(evt.includeServer(), new ModBlockLootProvider(dataGenerator, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModChestLootProvider(dataGenerator, ThinAir.MOD_ID));
        dataGenerator.addProvider(evt.includeServer(), new ModCraftingRecipes(dataGenerator));
        dataGenerator.addProvider(evt.includeClient(), new ModItemModels(dataGenerator, fileHelper));
        dataGenerator.addProvider(evt.includeServer(), new ModItemTagsProvider(evt, ThinAir.MOD_ID));
    }
}
