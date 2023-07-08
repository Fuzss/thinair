package fuzs.thinair.client;

import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.thinair.ThinAir;
import fuzs.thinair.client.renderer.entity.layers.RespiratorRenderer;
import fuzs.thinair.helper.AirHelper;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.Executor;

public class ThinAirClient implements ClientModConstructor {

    @Override
    public void onRegisterItemModelProperties(ItemModelPropertiesContext context) {
        context.registerItemProperty(ThinAir.id("air_quality"), (ItemStack stack, ClientLevel level, LivingEntity maybeEntity, int seed) -> {
            var entity = maybeEntity != null ? maybeEntity : stack.getEntityRepresentation();
            if (entity != null) {
                return switch (AirHelper.getO2LevelFromLocation(entity.getEyePosition(), entity.getLevel()).getFirst()) {
                    case RED -> 0.0F;
                    case YELLOW -> 0.1F;
                    case BLUE -> 0.2F;
                    case GREEN -> 0.3F;
                };
            } else {
                return 0.3F; // just do green?
            }
        }, ModRegistry.SAFETY_LANTERN_BLOCK.get());
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(RespiratorRenderer.PLAYER_RESPIRATOR_LAYER, () -> LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.02F), 0.0F), 64, 32));
    }

    @Override
    public void onRegisterClientReloadListeners(ClientReloadListenersContext context) {
        context.registerReloadListener("monocle_model", (PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) -> {
            return preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
                RespiratorRenderer.bakeModel(entityModels);
            }, executor2);
        });
    }
}
