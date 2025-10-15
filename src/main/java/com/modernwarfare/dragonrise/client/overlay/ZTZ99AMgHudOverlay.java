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

/**
 * ZTZ99A坦克机枪HUD渲染类
 * 负责渲染坦克机枪手的第一人称和第三人称准星及武器状态信息
 */
@OnlyIn(Dist.CLIENT)
public class ZTZ99AMgHudOverlay implements IGuiOverlay {

    // HUD的唯一标识符
    public static final String ID = Mod.MODID + "_ztz99a_mg_hud";

    // 资源路径定义
    private static final ResourceLocation MG_CROSSHAIR = new ResourceLocation("superbwarfare", "textures/screens/cannon/cannon_crosshair_notzoom.png");
    private static final ResourceLocation DRONE_ICON = new ResourceLocation("superbwarfare", "textures/screens/drone.png");

    /**
     * 主渲染方法 - 每帧调用
     * @param gui GUI实例
     * @param guiGraphics GUI图形上下文
     * @param partialTick 部分tick时间，用于平滑插值
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     */
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;

        // 检查是否应该渲染准星
        if (!shouldRenderCrossHair(player)) return;

        // 获取玩家所在的ZTZ99A实体
        ZTZ99AEntity ztz99a = (ZTZ99AEntity) player.getVehicle();
        if (ztz99a == null) return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // 设置渲染状态
        setupRenderState();

        // 根据相机类型选择不同的渲染方式
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            // 第一人称视角渲染
            renderFirstPersonCrosshair(guiGraphics, screenWidth, screenHeight, ztz99a, player);
        } else if (mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK) {
            // 第三人称视角渲染
            renderThirdPersonCrosshair(guiGraphics, ztz99a, player, partialTick);
        }

        poseStack.popPose();
        // 恢复渲染状态
        restoreRenderState();
    }

    /**
     * 渲染第一人称准星
     * @param guiGraphics GUI图形上下文
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     */
    private void renderFirstPersonCrosshair(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                            ZTZ99AEntity ztz99a, Player player) {

        // 计算FOV调整因子，确保准星在不同FOV下保持合适大小
        float fovAdjust = calculateFovAdjustment();

        // 计算准星渲染尺寸和位置
        float minDimension = (float) Math.min(screenWidth, screenHeight);
        float adjustedSize = Math.min((float) screenWidth / minDimension, (float) screenHeight / minDimension) * fovAdjust;
        int renderSize = Mth.floor(minDimension * adjustedSize);
        int xPos = (screenWidth - renderSize) / 2;  // 水平居中
        int yPos = (screenHeight - renderSize) / 2; // 垂直居中

        // 渲染机枪准星纹理
        preciseBlit(guiGraphics, MG_CROSSHAIR, xPos, yPos, 0, 0.0F, renderSize, renderSize, renderSize, renderSize);

        // 渲染武器状态信息（热量条等）
        renderWeaponStatus(guiGraphics, screenWidth, screenHeight, ztz99a, player);
    }

    /**
     * 渲染第三人称准星
     * @param guiGraphics GUI图形上下文
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     * @param partialTick 部分tick时间，用于平滑插值
     */
    private void renderThirdPersonCrosshair(GuiGraphics guiGraphics, ZTZ99AEntity ztz99a,
                                            Player player, float partialTick) {
        // 计算射击目标在世界中的位置（基于枪口位置和瞄准方向）
        Vec3 targetPos = calculateTargetPosition(ztz99a, player, partialTick);

        // 将世界坐标转换为屏幕坐标
        Vec3 screenPos = VectorUtil.worldToScreen(targetPos);

        // 检查目标是否在玩家视野内（没有被遮挡）
        if (VectorUtil.canSee(targetPos)) {
            // 在目标位置渲染第三人称准星
            renderThirdPersonCrosshairAtPosition(guiGraphics, screenPos, ztz99a, player);
        }
    }

    /**
     * 在指定屏幕位置渲染第三人称准星
     * @param guiGraphics GUI图形上下文
     * @param screenPos 屏幕坐标位置
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     */
    private void renderThirdPersonCrosshairAtPosition(GuiGraphics guiGraphics, Vec3 screenPos,
                                                      ZTZ99AEntity ztz99a, Player player) {
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;

        // 渲染无人机图标作为第三人称准星（24x24像素）
        preciseBlit(guiGraphics, DRONE_ICON, x - 12, y - 12, 0, 0, 24, 24, 24, 24);

        // 渲染第三人称武器信息
        renderThirdPersonWeaponInfo(guiGraphics, x, y, ztz99a, player);
    }

    /**
     * 计算FOV调整因子
     * 确保准星在不同FOV设置下保持相对一致的大小
     * @return FOV调整因子
     */
    private float calculateFovAdjustment() {
        // 以70度FOV为基准进行计算
        return (float) 70 / Minecraft.getInstance().options.fov().get();
    }

    /**
     * 计算目标位置（机枪瞄准的目标点）
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     * @param partialTick 部分tick时间
     * @return 目标点的世界坐标
     */
    private Vec3 calculateTargetPosition(ZTZ99AEntity ztz99a, Player player, float partialTick) {
        // 获取枪口位置
        Vec3 shootPos = ztz99a.passengerWeaponShootPos(player, partialTick);
        // 获取瞄准方向向量
        Vec3 aimVector = ztz99a.getGunVec(partialTick);
        // 计算192单位距离外的目标点
        return shootPos.add(aimVector.scale(192));
    }

    /**
     * 渲染武器状态信息
     * @param guiGraphics GUI图形上下文
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     */
    private void renderWeaponStatus(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                    ZTZ99AEntity ztz99a, Player player) {
        // 获取武器热量值
        int heat = ztz99a.getEntityData().get(ZTZ99AEntity.HEAT);

        // 仅在枪管过热时显示热量条
        if (heat > 0) {
            float heatPercent = heat / 100.0F;
            renderVerticalHeatBar(guiGraphics, screenWidth, screenHeight, heatPercent);
        }
    }

    /**
     * 渲染垂直热量条
     * @param guiGraphics GUI图形上下文
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @param heatPercent 热量百分比（0.0 - 1.0）
     */
    private void renderVerticalHeatBar(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float heatPercent) {
        int barWidth = 3;    // 热量条宽度
        int barHeight = 80;  // 热量条高度
        int margin = 20;     // 边距

        // 计算热量条位置（屏幕右侧）
        int x = screenWidth - margin - barWidth;
        int y = screenHeight / 2 - barHeight / 2;

        // 渲染背景（半透明黑色）
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0x80000000);

        // 计算热量填充高度
        int heatHeight = (int) (barHeight * heatPercent);
        // 根据热量百分比计算颜色（从绿色到红色）
        int heatColor = Mth.hsvToRgb(0F, heatPercent, 1.0F);
        // 渲染热量填充（从下往上增长）
        guiGraphics.fill(x, y + barHeight - heatHeight, x + barWidth, y + barHeight, heatColor | 0xFF000000);
    }

    /**
     * 渲染第三人称武器信息
     * @param guiGraphics GUI图形上下文
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @param ztz99a ZTZ99A实体实例
     * @param player 玩家实例
     */
    private void renderThirdPersonWeaponInfo(GuiGraphics guiGraphics, float x, float y,
                                             ZTZ99AEntity ztz99a, Player player) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        // 平移到准星位置
        poseStack.translate(x, y, 0);
        // 缩放文本大小
        poseStack.scale(0.75f, 0.75f, 1);

        // 这里可以添加第三人称视角下的武器信息显示
        // 例如：弹药数量、武器状态等

        poseStack.popPose();
    }

    /**
     * 设置渲染状态
     * 准备渲染HUD元素所需的OpenGL状态
     */
    private void setupRenderState() {
        RenderSystem.disableDepthTest();  // 禁用深度测试（HUD应该在最前面）
        RenderSystem.depthMask(false);    // 禁用深度写入
        RenderSystem.enableBlend();       // 启用混合（用于透明效果）
        RenderSystem.setShader(GameRenderer::getPositionTexShader); // 设置着色器
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,        // 源因子：源alpha
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, // 目标因子：1-源alpha
                GlStateManager.SourceFactor.ONE,              // 源alpha因子：1
                GlStateManager.DestFactor.ZERO                // 目标alpha因子：0
        );
        RenderSystem.setShaderColor(1, 1, 1, 1); // 设置着色器颜色为白色（不染色）
    }

    /**
     * 恢复渲染状态
     * 将OpenGL状态恢复为默认值，避免影响其他渲染
     */
    private void restoreRenderState() {
        RenderSystem.disableBlend();      // 禁用混合
        RenderSystem.defaultBlendFunc();  // 恢复默认混合函数
        RenderSystem.enableDepthTest();   // 启用深度测试
        RenderSystem.depthMask(true);     // 启用深度写入
    }

    /**
     * 检查是否应该渲染准星
     * @param player 玩家实例
     * @return 是否应该渲染准星
     */
    private boolean shouldRenderCrossHair(Player player) {
        if (player == null) return false;           // 玩家不存在
        if (player.isSpectator()) return false;     // 观察者模式不渲染

        // 仅当玩家在ZTZ99A的二号位（机枪手位置）时渲染
        return player.getVehicle() instanceof ZTZ99AEntity ztz99a &&
                ztz99a.getNthEntity(1) == player;  // 检查是否为二号位乘客
    }
}