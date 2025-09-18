package com.modernwarfare.globalstorm.client.renderer;

import com.modernwarfare.globalstorm.GlobalStorm;
import com.modernwarfare.globalstorm.client.model.AmmoBoxEntityModel;
import com.modernwarfare.globalstorm.init.ModModelLayer;
import com.modernwarfare.globalstorm.index.entity.AmmoBoxEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class AmmoBoxEntityRenderer extends EntityRenderer<AmmoBoxEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GlobalStorm.MODID, "textures/entity/ammo_box.png");

    public final AmmoBoxEntityModel<AmmoBoxEntity> model;

    public AmmoBoxEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new AmmoBoxEntityModel<>(pContext.bakeLayer(ModModelLayer.AMMO_BOX_LAYER));
    }

    @Override
    public void render(AmmoBoxEntity entity, float entityYaw, float partialTicks, PoseStack poseStack ,MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        poseStack.translate(0.0D, -1.5D, 0.0D);

        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        model.setupAnim(f, f1);

        model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AmmoBoxEntity pEntity) {
        return TEXTURE;
    }
}
