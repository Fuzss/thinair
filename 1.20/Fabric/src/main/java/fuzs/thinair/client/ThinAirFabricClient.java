package fuzs.thinair.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.thinair.ThinAir;
import net.fabricmc.api.ClientModInitializer;

public class ThinAirFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(ThinAir.MOD_ID, ThinAirClient::new);
    }
}
