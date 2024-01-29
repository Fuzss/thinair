package fuzs.thinair.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.ThinAirClient;
import fuzs.thinair.neoforge.integration.curios.NeoForgeCuriosClientIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ThinAir.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ThinAirClientNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(ThinAir.MOD_ID, ThinAirClient::new);
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        registerIntegrations();
    }

    private static void registerIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            NeoForgeCuriosClientIntegration.registerCuriosRenderer();
        }
    }
}
