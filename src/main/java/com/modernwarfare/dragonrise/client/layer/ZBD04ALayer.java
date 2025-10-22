package com.modernwarfare.dragonrise.client.layer;

import com.atsuishio.superbwarfare.Mod;
import com.modernwarfare.dragonrise.entity.ZBD04AEntity;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ZBD04ALayer extends GeoRenderLayer<ZBD04AEntity> {

    private static final ResourceLocation LAYER = Mod.loc("textures/entity/speedboat_e.png");

    public ZBD04ALayer(GeoRenderer<ZBD04AEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, ZBD04AEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.energySwirl(LAYER,0,1);
        poseStack.pushPose();
        //buffer.overlayCoords(10,10);
        //OverlayTexture.pack(10,10);
        //poseStack.translate(0, 0, -1F);
        //glowRenderType.
        //poseStack.
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
