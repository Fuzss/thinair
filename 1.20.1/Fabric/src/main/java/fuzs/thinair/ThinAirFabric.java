package fuzs.thinair;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.init.FabricModRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public class ThinAirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricModRegistry.touch();
        ModConfig modConfig = ForgeConfigRegistry.INSTANCE.register(ThinAir.MOD_ID,
                ModConfig.Type.COMMON,
                CommonConfig.SPEC);
        ModConfigEvents.loading(ThinAir.MOD_ID).register((ModConfig config) -> {
            if (config == modConfig) {
                CommonConfig.onModConfigLoading();
            }
        });
        ModConfigEvents.reloading(ThinAir.MOD_ID).register((ModConfig config) -> {
            if (config == modConfig) {
                CommonConfig.onModConfigLoading();
            }
        });
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
    }
}
