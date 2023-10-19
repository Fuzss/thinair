package fuzs.thinair.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.thinair.ThinAir;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RespiratorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation RESPIRATOR_LOCATION = ThinAir.id("textures/models/armor/respirator_layer_1.png");

    private final A model;

    public RespiratorLayer(RenderLayerParent<T, M> renderer, A model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.is(ModRegistry.RESPIRATOR_ITEM.get())) {

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.setAllVisible(false);
            this.model.head.visible = true;
            this.model.hat.visible = true;

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(RESPIRATOR_LOCATION));
            this.model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, (float) 1.0, (float) 1.0, (float) 1.0, 1.0F);

            if (itemStack.hasFoil()) {
                this.model.renderToBuffer(matrixStack, buffer.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}

