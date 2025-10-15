package com.modernwarfare.dragonrise.client.renderer.entity;

import com.modernwarfare.dragonrise.client.model.ZHI10MEModel;
import com.modernwarfare.dragonrise.entity.ZHI10MEEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZHI10MERenderer extends GeoEntityRenderer<ZHI10MEEntity> {
    public ZHI10MERenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZHI10MEModel());
        this.shadowRadius = 0.5f;
    }

    public RenderType getRenderType(ZHI10MEEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack,ZHI10MEEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        float scale = 0.75F;
//        this.scaleHeight = scale;
//        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(ZHI10MEEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3((double)0.0F, 1.45, (double)0.0F);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float)root.x, (float)root.y, (float)root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float)root.x, (float)root.y, (float)root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float)root.x, (float)root.y, (float)root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, ZHI10MEEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("lxj")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }

        if (name.equals("wj")) {
            bone.setRotX(-6.0F * Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        if(name.equals("gun")){
            bone.setRotY(Mth.lerp(partialTick, animatable.gunYRotO, animatable.getGunYRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("bone6")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setRotX((float) Mth.clamp(
                    -Mth.lerp(partialTick, animatable.gunXRotO, animatable.getGunXRot()) * Mth.DEG_TO_RAD,
                    -80 * Mth.DEG_TO_RAD, 2.5 * Mth.DEG_TO_RAD)
            );
        }
        // 光电球支架 - 水平转动
        if (name.equals("lightboll")) {
            float gunYRot = Mth.lerp(partialTick, animatable.gunYRotO, animatable.getGunYRot());
            bone.setRotY(gunYRot * Mth.DEG_TO_RAD);
        }

        // 光电球本体 - 竖直转动
        if (name.equals("boll")) {
            float gunXRot = Mth.lerp(partialTick, animatable.gunXRotO, animatable.getGunXRot());
            float clampedXRot = Mth.clamp(gunXRot, -60.0F, 30.0F);  // 限制俯仰角度
            bone.setRotX(-clampedXRot * Mth.DEG_TO_RAD);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
