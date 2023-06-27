package fuzs.thinair.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.thinair.ThinAir;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ThinAir.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ThinAirClientForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(ThinAir.MOD_ID, ThinAirClient::new);
    }
}
