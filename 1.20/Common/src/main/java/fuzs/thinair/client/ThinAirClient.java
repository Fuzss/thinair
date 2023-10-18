package fuzs.thinair.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelPropertiesContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.event.v1.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.ClientLevelTickEvents;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.handler.ClientAirBubbleTracker;
import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ThinAirClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientChunkEvents.LOAD.register(ClientAirBubbleTracker::onChunkLoad);
        ClientLevelTickEvents.END.register(ClientAirBubbleTracker::onEndLevelTick);
        ClientLevelEvents.UNLOAD.register(ClientAirBubbleTracker::onLevelUnload);
    }

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItemProperty(ThinAir.id("air_quality"), (ItemStack stack, ClientLevel level, LivingEntity maybeEntity, int seed) -> {
            var entity = maybeEntity != null ? maybeEntity : stack.getEntityRepresentation();
            if (entity != null) {
                return switch (AirHelper.getAirQualityAtLocation(entity.getEyePosition(), entity.level())) {
                    case RED -> 0;
                    case YELLOW -> 1;
                    case BLUE -> 2;
                    case GREEN -> 3;
                };
            } else {
                return 3; // just do green?
            }
        }, ModRegistry.SAFETY_LANTERN_BLOCK.get().asItem());
    }

    @Override
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        context.registerRenderType(RenderType.cutout(), ModRegistry.SIGNAL_TORCH_BLOCK.get(), ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get());
    }
}
