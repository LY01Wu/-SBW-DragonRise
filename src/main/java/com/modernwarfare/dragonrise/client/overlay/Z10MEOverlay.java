package com.modernwarfare.dragonrise.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClickHandler;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.VectorUtil;
import com.modernwarfare.dragonrise.entity.ZHI10MEEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Math;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;

@OnlyIn(Dist.CLIENT)
public class Z10MEOverlay implements IGuiOverlay {
    public static final String ID = "superbwarfare_helicopter_hud";
    private static float scopeScale = 1.0F;
    private static float lerpVy = 1.0F;
    private static float lerpPower = 1.0F;

    public Z10MEOverlay() {
    }

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        PoseStack poseStack = guiGraphics.pose();
        if (player != null) {
            if (!ClickHandler.isEditing) {
                Entity f = player.getVehicle();
                if (f instanceof HelicopterEntity iHelicopterEntity) {
                    f = player.getVehicle();
                    if (f instanceof MobileVehicleEntity mobileVehicle) {
                        if(f instanceof ZHI10MEEntity zhi10MEEntity) {
                            if (player == zhi10MEEntity.getNthEntity(1)) {
                                float minWH = (float) Math.min(screenWidth, screenHeight);
                                float scaledMinWH = Mth.floor(minWH * 1);
                                float centerW = ((screenWidth - scaledMinWH) / 2);
                                float centerH = ((screenHeight - scaledMinWH) / 2);
                                RenderSystem.disableDepthTest();
                                RenderSystem.depthMask(false);
                                RenderSystem.enableBlend();
                                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                                RenderSystem.setShaderColor(1, 1, 1, 1);

                                preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
                            }
                        }
                        if (iHelicopterEntity.isDriver(player)) {
                            f = player.getVehicle();
                            if (f instanceof WeaponVehicleEntity weaponVehicle) {
                                poseStack.pushPose();
                                poseStack.translate((double)-6.0F * ClientEventHandler.turnRot[1], (double)-6.0F * ClientEventHandler.turnRot[0], 0.0F);
                                RenderSystem.disableDepthTest();
                                RenderSystem.depthMask(false);
                                RenderSystem.enableBlend();
                                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                scopeScale = Mth.lerp(partialTick, scopeScale, 1.0F);
                                float f0 = (float)Math.min(screenWidth, screenHeight);
                                float f1 = Math.min((float)screenWidth / f0, (float)screenHeight / f0) * scopeScale;
                                float i = (float)Mth.floor(f0 * f1);
                                float j = (float)Mth.floor(f0 * f1);
                                float k = ((float)screenWidth - i) / 2.0F;
                                float l = ((float)screenHeight - j) / 2.0F;
                                if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/heli_base.png"), k, l, 0.0F, 0.0F, i, j, i, j);
                                    renderDriverAngle(guiGraphics, player, mobileVehicle, k, l, i, j, partialTick);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/compass.png"), (float)screenWidth / 2.0F - 128.0F, 6.0F, 128.0F + 1.4222223F * mobileVehicle.getYRot(), 0.0F, 256.0F, 16.0F, 512.0F, 16.0F);
                                    poseStack.pushPose();
                                    poseStack.rotateAround(Axis.ZP.rotationDegrees(-iHelicopterEntity.getRotZ(partialTick)), (float)screenWidth / 2.0F, (float)screenHeight / 2.0F, 0.0F);
                                    float pitch = iHelicopterEntity.getRotX(partialTick);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/heli_line.png"), (float)screenWidth / 2.0F - 128.0F, (float)screenHeight / 2.0F - 512.0F - 5.475F * pitch, 0.0F, 0.0F, 256.0F, 1024.0F, 256.0F, 1024.0F);
                                    poseStack.popPose();
                                    poseStack.pushPose();
                                    poseStack.rotateAround(Axis.ZP.rotationDegrees(iHelicopterEntity.getRotZ(partialTick)), (float)screenWidth / 2.0F, (float)screenHeight / 2.0F - 56.0F, 0.0F);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/roll_ind.png"), (float)screenWidth / 2.0F - 8.0F, (float)screenHeight / 2.0F - 88.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, 16.0F);
                                    poseStack.popPose();
                                    guiGraphics.blit(Mod.loc("textures/screens/helicopter/heli_power_ruler.png"), screenWidth / 2 + 100, screenHeight / 2 - 64, 0.0F, 0.0F, 64, 128, 64, 128);
                                    double height = mobileVehicle.position().distanceTo(Vec3.atLowerCornerOf(mobileVehicle.level().clip(new ClipContext(mobileVehicle.position(), mobileVehicle.position().add((new Vec3(0.0F, -1.0F, 0.0F)).scale(100.0F)), Block.OUTLINE, Fluid.ANY, mobileVehicle)).getBlockPos()));
                                    double blockInWay = mobileVehicle.position().distanceTo(Vec3.atLowerCornerOf(mobileVehicle.level().clip(new ClipContext(mobileVehicle.position(), mobileVehicle.position().add((new Vec3(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y + 0.06, mobileVehicle.getDeltaMovement().z)).normalize().scale(100.0F)), Block.OUTLINE, Fluid.ANY, mobileVehicle)).getBlockPos()));
                                    float power = iHelicopterEntity.getPower();
                                    lerpPower = Mth.lerp(0.001F * partialTick, lerpPower, power);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/heli_power.png"), (float)screenWidth / 2.0F + 130.0F, (float)screenHeight / 2.0F - 64.0F + 124.0F - power * 980.0F, 0.0F, 0.0F, 4.0F, power * 980.0F, 4.0F, power * 980.0F);
                                    lerpVy = (float)Mth.lerp(0.021F * partialTick, lerpVy, mobileVehicle.getDeltaMovement().y());
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/heli_vy_move.png"), (float)screenWidth / 2.0F + 138.0F, (float)screenHeight / 2.0F - 3.0F - Math.max(lerpVy * 20.0F, -24.0F) * 2.5F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F);
                                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.format0D(lerpVy * 20.0F, "m/s")), screenWidth / 2 + 146, (int)((double)((float)screenHeight / 2.0F - 3.0F) - (double)Math.max(lerpVy * 20.0F, -24.0F) * (double)2.5F), !(lerpVy * 20.0F < -24.0F) && (!(lerpVy * 20.0F < -10.0F) && (!(lerpVy * 20.0F < -1.0F) || !(length(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y, mobileVehicle.getDeltaMovement().z) * (double)72.0F > (double)100.0F)) || !(height < (double)36.0F)) && (!(length(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y, mobileVehicle.getDeltaMovement().z) * (double)72.0F > (double)40.0F) || !(blockInWay < (double)72.0F)) ? 6749952 : -65536, false);
                                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.format0D(mobileVehicle.getY())), screenWidth / 2 + 104, screenHeight / 2, 6749952, false);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/speed_frame.png"), (float)screenWidth / 2.0F - 144.0F, (float)screenHeight / 2.0F - 6.0F, 0.0F, 0.0F, 50.0F, 18.0F, 50.0F, 18.0F);
                                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.format0D(length(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y, mobileVehicle.getDeltaMovement().z) * (double)72.0F, "km/h")), screenWidth / 2 - 140, screenHeight / 2, 6749952, false);
                                    if (mobileVehicle instanceof ZHI10MEEntity zhi10me) {
                                        if (weaponVehicle.getWeaponIndex(0) == 0) {
                                            double heat = 1.0F - (float) zhi10me.getEntityData().get(MobileVehicleEntity.HEAT) / 100.0F;
                                            Font var10001 = Minecraft.getInstance().font;
                                            Object var10002 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : iHelicopterEntity.getAmmoCount(player);
                                            guiGraphics.drawString(var10001, Component.literal("70MM ROCKET " + var10002), screenWidth / 2 - 160, screenHeight / 2 - 60, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
                                        } else {
                                            Font var34 = Minecraft.getInstance().font;
                                            int var37 = iHelicopterEntity.getAmmoCount(player);
                                            guiGraphics.drawString(var34, Component.literal("70MM ROCKET 2 " + var37), screenWidth / 2 - 160, screenHeight / 2 - 60, 6749952, false);
                                        }
                                    }

                                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("FLARE " + iHelicopterEntity.getDecoy()), screenWidth / 2 - 160, screenHeight / 2 - 50, 6749952, false);
                                    if (lerpVy * 20.0F < -24.0F) {
                                        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("SINK RATE，PULL UP!"), screenWidth / 2 - 53, screenHeight / 2 + 24, -65536, false);
                                    } else if ((lerpVy * 20.0F < -10.0F || lerpVy * 20.0F < -1.0F && length(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y, mobileVehicle.getDeltaMovement().z) * (double)72.0F > (double)100.0F) && height < (double)36.0F || length(mobileVehicle.getDeltaMovement().x, mobileVehicle.getDeltaMovement().y, mobileVehicle.getDeltaMovement().z) * (double)72.0F > (double)40.0F && blockInWay < (double)72.0F) {
                                        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("TERRAIN TERRAIN"), screenWidth / 2 - 42, screenHeight / 2 + 24, -65536, false);
                                    }

                                    if (mobileVehicle.hasEnergyStorage()) {
                                        if ((double)mobileVehicle.getEnergy() < 0.02 * (double)mobileVehicle.getMaxEnergy()) {
                                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("NO POWER!"), screenWidth / 2 - 144, screenHeight / 2 + 14, -65536, false);
                                        } else if ((double)mobileVehicle.getEnergy() < 0.2 * (double)mobileVehicle.getMaxEnergy()) {
                                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("LOW POWER"), screenWidth / 2 - 144, screenHeight / 2 + 14, 16739072, false);
                                        }
                                    }
                                }

                                Vec3 pos = iHelicopterEntity.shootPos(partialTick).add(iHelicopterEntity.shootVec(partialTick).scale(192.0F));
                                Vec3 p = VectorUtil.worldToScreen(pos);
                                poseStack.pushPose();
                                float x = (float)p.x;
                                float y = (float)p.y;
                                if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/crosshair_ind.png"), x - 8.0F, y - 8.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, 16.0F);
                                    renderKillIndicator(guiGraphics, x - 7.5F + (float)((double)2.0F * (Math.random() - (double)0.5F)), y - 7.5F + (float)((double)2.0F * (Math.random() - (double)0.5F)));
                                } else if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
                                    poseStack.pushPose();
                                    poseStack.rotateAround(Axis.ZP.rotationDegrees(iHelicopterEntity.getRotZ(partialTick)), x, y, 0.0F);
                                    RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/drone.png"), x - 8.0F, y - 8.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, 16.0F);
                                    renderKillIndicator(guiGraphics, x - 7.5F + (float)((double)2.0F * (Math.random() - (double)0.5F)), y - 7.5F + (float)((double)2.0F * (Math.random() - (double)0.5F)));
                                    poseStack.pushPose();
                                    poseStack.translate(x, y, 0.0F);
                                    poseStack.scale(0.75F, 0.75F, 1.0F);
                                    if (mobileVehicle instanceof ZHI10MEEntity zhi10me) {
                                        if (weaponVehicle.getWeaponIndex(0) == 0) {
                                            double heat = (float) zhi10me.getEntityData().get(MobileVehicleEntity.HEAT) / 100.0F;
                                            Font var35 = Minecraft.getInstance().font;
                                            Object var38 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : iHelicopterEntity.getAmmoCount(player);
                                            guiGraphics.drawString(var35, Component.literal("70MM ROCKET " + var38), 25, -9, Mth.hsvToRgb(0.0F, (float)heat, 1.0F), false);
                                        } else {
                                            Font var36 = Minecraft.getInstance().font;
                                            int var39 = iHelicopterEntity.getAmmoCount(player);
                                            guiGraphics.drawString(var36, Component.literal("70MM ROCKET 4 " + var39), 25, -9, -1, false);
                                        }
                                    }

                                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("FLARE " + iHelicopterEntity.getDecoy()), 25, 1, -1, false);
                                    poseStack.popPose();
                                    poseStack.popPose();
                                }

                                poseStack.popPose();
                                poseStack.popPose();
                                return;
                            }
                        }

                    }
                }

                scopeScale = 0.7F;
            }
        }
    }

    private static void renderKillIndicator(GuiGraphics guiGraphics, float posX, float posY) {
        VehicleHudOverlay.renderKillIndicator3P(guiGraphics, posX, posY);
    }

    private static void renderDriverAngle(GuiGraphics guiGraphics, Player player, Entity heli, float k, float l, float i, float j, float ticks) {
        float diffY = Mth.wrapDegrees(Mth.lerp(ticks, player.yHeadRotO, player.getYHeadRot()) - Mth.lerp(ticks, heli.yRotO, heli.getYRot())) * 0.35F;
        float diffX = Mth.wrapDegrees(Mth.lerp(ticks, player.xRotO, player.getXRot()) - Mth.lerp(ticks, heli.xRotO, heli.getXRot())) * 0.072F;
        RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/helicopter/heli_driver_angle.png"), k + diffY, l + diffX, 0.0F, 0.0F, i, j, i, j);
    }

    public static double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
