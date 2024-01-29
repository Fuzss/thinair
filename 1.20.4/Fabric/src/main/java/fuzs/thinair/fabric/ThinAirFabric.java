package fuzs.thinair.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.thinair.ThinAir;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.fabric.init.FabricModRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class ThinAirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricModRegistry.touch();
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
        NeoForgeConfigRegistry.INSTANCE.register(ThinAir.MOD_ID, ModConfig.Type.COMMON, CommonConfig.SPEC);
    }
}
