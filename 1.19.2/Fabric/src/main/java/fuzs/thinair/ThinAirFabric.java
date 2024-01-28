package fuzs.thinair;

import fuzs.puzzleslib.core.CommonFactories;
import fuzs.thinair.advancements.ModAdvancementTriggers;
import fuzs.thinair.world.level.block.SignalTorchBlock;
import fuzs.thinair.handler.AirBubbleTracker;
import fuzs.thinair.config.CommonConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ThinAirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(ThinAir.MOD_ID, ModConfig.Type.COMMON, CommonConfig.SPEC);
        CommonFactories.INSTANCE.modConstructor(ThinAir.MOD_ID).accept(new ThinAir());
        ModAdvancementTriggers.registerTriggers(CriteriaTriggers::register);
        registerHandlers();
    }

    private static void registerHandlers() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // TODO handle return result
            SignalTorchBlock.switchTorchType(player, world, hand, hitResult);
            return InteractionResult.PASS;
        });
        ServerChunkEvents.CHUNK_LOAD.register(AirBubbleTracker::onChunkLoadServer);
        ServerWorldEvents.UNLOAD.register(AirBubbleTracker::onWorldClose);
        ServerTickEvents.END_WORLD_TICK.register(AirBubbleTracker::consumeReqdChunksServer);
        // TODO add DrownedOxygent::onLivingHurt
        // TODO add TickAirChecker.modifyEntityAir
    }
}
