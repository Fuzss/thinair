package fuzs.thinair.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelPropertiesContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.event.v1.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.ClientLevelTickEvents;
import fuzs.thinair.ThinAir;
import fuzs.thinair.api.AirQualityHelper;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.client.handler.ClientAirBubbleTracker;
import fuzs.thinair.init.ModRegistry;
import fuzs.thinair.world.level.block.SafetyLanternBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
        context.registerItemProperty(ThinAir.id("air_quality_level"), (ItemStack itemStack, ClientLevel level, LivingEntity entity, int seed) -> {
            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag != null && compoundTag.contains(SafetyLanternBlock.TAG_AIR_QUALITY_LEVEL, Tag.TAG_INT)) {
                int airQualityLevel = compoundTag.getInt(SafetyLanternBlock.TAG_AIR_QUALITY_LEVEL);
                return AirQualityLevel.values()[airQualityLevel].getItemModelProperty();
            }
            if (entity == null && itemStack.getEntityRepresentation() instanceof LivingEntity livingEntity) {
                entity = livingEntity;
            }
            AirQualityLevel airQualityAtLocation;
            if (entity != null) {
                airQualityAtLocation = AirQualityHelper.INSTANCE.getAirQualityAtLocation(entity);
            } else {
                airQualityAtLocation = AirQualityLevel.YELLOW;
            }
            return airQualityAtLocation.getItemModelProperty();
        }, ModRegistry.SAFETY_LANTERN_BLOCK.get());
    }

    @Override
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        context.registerRenderType(RenderType.cutout(), ModRegistry.SIGNAL_TORCH_BLOCK.get(), ModRegistry.WALL_SIGNAL_TORCH_BLOCK.get(), ModRegistry.SAFETY_LANTERN_BLOCK.get());
    }
}
