package fuzs.thinair.client.renderer.entity.layers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.client.model.geom.ModelLayerRegistry;
import fuzs.thinair.ThinAir;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class RespiratorRenderer {
    private static final ResourceLocation TEXTURE_LOCATION = ThinAir.id("textures/models/armor/respirator_layer_1.png");
    private static final ModelLayerRegistry LAYER_REGISTRY = ClientFactories.INSTANCE.modelLayerRegistration(ThinAir.MOD_ID);
    public static final ModelLayerLocation PLAYER_RESPIRATOR_LAYER = LAYER_REGISTRY.register("player", "respirator");

    private static RespiratorRenderer instance;

    private final HumanoidModel<LivingEntity> model;

    private RespiratorRenderer(EntityModelSet entityModels) {
        // nice trick from artifacts mod for overriding those two methods and therefore being able to use the vanilla class
        this.model = new HumanoidModel<>(entityModels.bakeLayer(PLAYER_RESPIRATOR_LAYER)) {

            @Override
            protected Iterable<ModelPart> headParts() {
                return ImmutableList.of(this.head);
            }

            @Override
            protected Iterable<ModelPart> bodyParts() {
                return ImmutableList.of();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends LivingEntity> void render(ItemStack stack, PoseStack matrixStack, EntityModel<? extends LivingEntity> entityModel, MultiBufferSource multiBufferSource, int light, Function<ResourceLocation, RenderType> renderType) {
        ((HumanoidModel<T>) entityModel).copyPropertiesTo((HumanoidModel<T>) this.model);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, renderType.apply(TEXTURE_LOCATION), false, stack.hasFoil());
        this.model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static RespiratorRenderer get() {
        return instance;
    }

    public static void bakeModel(EntityModelSet entityModels) {
        instance = new RespiratorRenderer(entityModels);
    }
}
