package fuzs.thinair.client;

import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.handler.ClientAirBubbleTracker;
import fuzs.thinair.integration.trinkets.TrinketsClientIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ThinAirFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(ThinAir.MOD_ID).accept(new ThinAirClient());
        registerHandlers();
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("trinkets")) {
            TrinketsClientIntegration.registerTrinketsRenderer();
        }
    }

    private static void registerHandlers() {
        ClientChunkEvents.CHUNK_LOAD.register(ClientAirBubbleTracker::onChunkLoadClient);
        // TODO missing call to AirBubbleTracker::onWorldClose
        ClientTickEvents.END_CLIENT_TICK.register(ClientAirBubbleTracker::consumeReqdChunksClient);
    }
}
