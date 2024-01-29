package fuzs.thinair.forge;

import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.forge.api.capability.v3.ForgeCapabilityHelper;
import fuzs.thinair.ThinAir;
import fuzs.thinair.config.CommonConfig;
import fuzs.thinair.forge.init.ForgeModRegistry;
import fuzs.thinair.forge.integration.curios.ForgeCuriosIntegration;
import fuzs.thinair.init.ModRegistry;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(ThinAir.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThinAirForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModConstructor.construct(ThinAir.MOD_ID, ThinAir::new);
        NeoForgeConfigRegistry.INSTANCE.register(ModConfig.Type.COMMON, CommonConfig.SPEC);
        registerCapabilities();
        registerIntegrations();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY, new CapabilityToken<>() {
            // NO-OP
        });
    }

    private static void registerIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("curios")) {
            ForgeCuriosIntegration.registerHandlers();
        }
    }
}
