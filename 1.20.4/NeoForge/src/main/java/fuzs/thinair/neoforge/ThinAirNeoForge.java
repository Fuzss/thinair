package fuzs.thinair.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.thinair.ThinAir;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.data.ModAdvancementProvider;
import fuzs.thinair.data.tags.ModItemTagProvider;
import fuzs.thinair.data.client.ModLanguageProvider;
import fuzs.thinair.data.client.ModModelProvider;
import fuzs.thinair.data.loot.ModBlockLootProvider;
import fuzs.thinair.data.loot.ModChestLootProvider;
import fuzs.thinair.data.tags.ModBlockTagProvider;
import fuzs.thinair.data.tags.ModEntityTypeTagProvider;
import fuzs.thinair.data.ModRecipeProvider;
import fuzs.thinair.neoforge.init.NeoForgeModRegistry;
import fuzs.thinair.neoforge.integration.curios.NeoForgeCuriosIntegration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        NeoForgeModRegistry.touch();
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        registerIntegrations();
        DataProviderHelper.registerDataProviders(ThinAir.MOD_ID,
                ModAdvancementProvider::new,
                ModBlockLootProvider::new,
                ModBlockTagProvider::new,
                ModChestLootProvider::new,
                ModEntityTypeTagProvider::new,
                ModItemTagProvider::new,
                ModRecipeProvider::new
        );
        DataProviderHelper.registerDataProviders(ThinAir.MOD_ID, ModLanguageProvider::new, ModModelProvider::new);
    }

    private static void registerIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            NeoForgeCuriosIntegration.registerHandlers();
        }
    }
}
