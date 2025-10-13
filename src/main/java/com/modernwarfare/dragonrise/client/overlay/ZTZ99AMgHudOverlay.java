package com.modernwarfare.dragonrise.client.overlay;

import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import com.atsuishio.superbwarfare.tools.VectorUtil;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;

@OnlyIn(Dist.CLIENT)
public class ZTZ99AMgHudOverlay implements IGuiOverlay {

    public static final String ID = Mod.MODID + "_ztz99a_mg_hud";

    private static final ResourceLocation MG_CROSSHAIR = new ResourceLocation("superbwarfare", "textures/screens/cannon/cannon_crosshair_notzoom.png");
    private static final ResourceLocation DRONE_ICON = new ResourceLocation("superbwarfare", "textures/screens/drone.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;

        if (!shouldRenderCrossHair(player)) return;

        ZTZ99AEntity ztz99a = (ZTZ99AEntity) player.getVehicle();
        if (ztz99a == null) return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        setupRenderState();

        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            renderFirstPersonCrosshair(guiGraphics, screenWidth, screenHeight, ztz99a, player);
        } else if (mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK) {
            renderThirdPersonCrosshair(guiGraphics, ztz99a, player, partialTick);
        }

        poseStack.popPose();
        restoreRenderState();
    }

    private void renderFirstPersonCrosshair(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                            ZTZ99AEntity ztz99a, Player player) {

        float fovAdjust = calculateFovAdjustment();

        float minDimension = (float) Math.min(screenWidth, screenHeight);
        float adjustedSize = Math.min((float) screenWidth / minDimension, (float) screenHeight / minDimension) * fovAdjust;
        int renderSize = Mth.floor(minDimension * adjustedSize);
        int xPos = (screenWidth - renderSize) / 2;
        int yPos = (screenHeight - renderSize) / 2;

        preciseBlit(guiGraphics, MG_CROSSHAIR, xPos, yPos, 0, 0.0F, renderSize, renderSize, renderSize, renderSize);

        renderWeaponStatus(guiGraphics, screenWidth, screenHeight, ztz99a, player);
    }

    private void renderThirdPersonCrosshair(GuiGraphics guiGraphics, ZTZ99AEntity ztz99a,
                                            Player player, float partialTick) {
        // 计算射击目标在世界中的位置
        Vec3 targetPos = calculateTargetPosition(ztz99a, player, partialTick);

        // 转换为屏幕坐标
        Vec3 screenPos = VectorUtil.worldToScreen(targetPos);

        // 检查目标是否在视野内
        if (VectorUtil.canSee(targetPos)) {
            renderThirdPersonCrosshairAtPosition(guiGraphics, screenPos, ztz99a, player);
        }
    }

    private void renderThirdPersonCrosshairAtPosition(GuiGraphics guiGraphics, Vec3 screenPos,
                                                      ZTZ99AEntity ztz99a, Player player) {
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;

        preciseBlit(guiGraphics, DRONE_ICON, x - 12, y - 12, 0, 0, 24, 24, 24, 24);

        renderThirdPersonWeaponInfo(guiGraphics, x, y, ztz99a, player);
    }

    private float calculateFovAdjustment() {
        return (float) 70 / Minecraft.getInstance().options.fov().get();
    }

    private Vec3 calculateTargetPosition(ZTZ99AEntity ztz99a, Player player, float partialTick) {
        Vec3 shootPos = ztz99a.passengerWeaponShootPos(player, partialTick);
        Vec3 aimVector = ztz99a.getGunVec(partialTick);
        return shootPos.add(aimVector.scale(192)); // 192单位距离
    }

    private void renderWeaponStatus(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                    ZTZ99AEntity ztz99a, Player player) {
        int heat = ztz99a.getEntityData().get(ZTZ99AEntity.HEAT);

        // 仅在枪管过热时显示热量条
        if (heat > 0) {
            float heatPercent = heat / 100.0F;
            renderVerticalHeatBar(guiGraphics, screenWidth, screenHeight, heatPercent);
        }
    }

    private void renderVerticalHeatBar(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float heatPercent) {
        int barWidth = 3;
        int barHeight = 80;
        int margin = 20;

        int x = screenWidth - margin - barWidth;
        int y = screenHeight / 2 - barHeight / 2;

        // 背景（半透明黑色）
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0x80000000);

        // 热量填充（从下往上）
        int heatHeight = (int) (barHeight * heatPercent);
        int heatColor = Mth.hsvToRgb(0F, heatPercent, 1.0F);
        guiGraphics.fill(x, y + barHeight - heatHeight, x + barWidth, y + barHeight, heatColor | 0xFF000000);

    }

    private void renderThirdPersonWeaponInfo(GuiGraphics guiGraphics, float x, float y,
                                             ZTZ99AEntity ztz99a, Player player) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(0.75f, 0.75f, 1);

        poseStack.popPose();
    }

    private void setupRenderState() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void restoreRenderState() {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }

    private boolean shouldRenderCrossHair(Player player) {
        if (player == null) return false;
        if (player.isSpectator()) return false;

        // 仅当玩家在ZTZ99A的二号位时渲染
        return player.getVehicle() instanceof ZTZ99AEntity ztz99a &&
                ztz99a.getNthEntity(1) == player;
    }
}