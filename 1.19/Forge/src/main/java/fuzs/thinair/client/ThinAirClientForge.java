package fuzs.thinair.client;

import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.handler.ClientAirBubbleTracker;
import fuzs.thinair.integration.curios.CuriosClientIntegration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ThinAir.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ThinAirClientForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(ThinAir.MOD_ID).accept(new ThinAirClient());
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final ChunkEvent.Load evt) -> {
            if (!evt.getLevel().isClientSide()) return;
            ClientAirBubbleTracker.onChunkLoadClient(evt.getLevel(), evt.getChunk());
        });
        MinecraftForge.EVENT_BUS.addListener((final LevelEvent.Unload evt) -> {
            if (!evt.getLevel().isClientSide()) return;
            ClientAirBubbleTracker.onWorldClose(Minecraft.getInstance(), evt.getLevel());
        });
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.ClientTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            ClientAirBubbleTracker.consumeReqdChunksClient(Minecraft.getInstance());
        });
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            CuriosClientIntegration.registerCuriosRenderer();
        }
    }
}
