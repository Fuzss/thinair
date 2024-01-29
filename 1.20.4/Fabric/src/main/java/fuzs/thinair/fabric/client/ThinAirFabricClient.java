package fuzs.thinair.fabric.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.ThinAirClient;
import fuzs.thinair.fabric.integration.trinkets.TrinketsClientIntegration;
import net.fabricmc.api.ClientModInitializer;

public class ThinAirFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(ThinAir.MOD_ID, ThinAirClient::new);
        registerIntegrations();
    }

    private static void registerIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("trinkets")) {
            TrinketsClientIntegration.registerTrinketsRenderer();
        }
    }
}
