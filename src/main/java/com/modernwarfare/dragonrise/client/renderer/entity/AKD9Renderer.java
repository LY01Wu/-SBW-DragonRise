package com.modernwarfare.dragonrise.client.renderer.entity;

import com.modernwarfare.dragonrise.client.layer.AKD9Layer;
import com.modernwarfare.dragonrise.client.model.AKD9Model;
import com.modernwarfare.dragonrise.entity.AKD9Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AKD9Renderer extends GeoEntityRenderer<AKD9Entity> {
    public AKD9Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AKD9Model());
        this.addRenderLayer(new AKD9Layer(this));
    }

    public RenderType getRenderType(AKD9Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void render(AKD9Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    protected float getDeathMaxRotation(AKD9Entity entityLivingBaseIn) {
        return 0.0F;
    }
}
