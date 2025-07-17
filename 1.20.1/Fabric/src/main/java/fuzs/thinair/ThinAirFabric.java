package fuzs.thinair;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.thinair.init.FabricModRegistry;
import net.fabricmc.api.ModInitializer;

public class ThinAirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricModRegistry.touch();
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
    }
}
