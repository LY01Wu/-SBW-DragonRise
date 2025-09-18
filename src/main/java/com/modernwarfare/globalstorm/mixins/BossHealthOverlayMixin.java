package com.modernwarfare.globalstorm.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.*;

import java.util.Map;
import java.util.UUID;


@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static final int SQUARE_SIZE = 20;

    @Unique
    private static final int MARGIN = 10;

    @Unique
    private static final int MAX_DISPLAYED = 10;

    /**
     * 修改原版render（史山代码）
     */
    @Overwrite
    public void render(GuiGraphics guiGraphics) {

        if (!events.isEmpty()) {
            int screenWidth = guiGraphics.guiWidth();
            int totalSquares = Math.min(events.size(), MAX_DISPLAYED);
            int totalWidth = totalSquares * (SQUARE_SIZE + MARGIN) - MARGIN;
            int startX = (screenWidth - totalWidth) / 2;
            int y = 10;

            int index = 0;
            for (LerpingBossEvent event : events.values()) {
                if (index >= MAX_DISPLAYED) break;

                int x = startX + index * (SQUARE_SIZE + MARGIN);

                // 触发Forge事件
                var forgeEvent = net.minecraftforge.client.ForgeHooksClient.onCustomizeBossEventProgress(
                        guiGraphics, minecraft.getWindow(), event, x, y, 10 + minecraft.font.lineHeight);

                if (!forgeEvent.isCanceled()) {
                    this.dragonrise$renderSquaredBossBar(guiGraphics, x, y, event);
                }

                index++;
            }
        }
    }

    /**
     * 渲染方形bossbar框
     */
    @Unique
    private void dragonrise$renderSquaredBossBar(GuiGraphics guiGraphics, int x, int y, LerpingBossEvent bossEvent) {
        // 绘制背景方框
        //guiGraphics.fill(x, y, x + SQUARE_SIZE, y + SQUARE_SIZE, 0xFF000000);
        //guiGraphics.fill(x + 1, y + 1, x + SQUARE_SIZE - 1, y + SQUARE_SIZE - 1, 0xFF444444);
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        int centerX = x + SQUARE_SIZE / 2;
        int centerY = y + SQUARE_SIZE / 2;

        poseStack.translate(centerX, centerY, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        poseStack.translate(-centerX, -centerY, 0);

        // 计算进度填充
        float progress = bossEvent.getProgress();
        //int fillHeight = (int) (SQUARE_SIZE * progress);

        if (progress > 0) {
            // 根据Boss颜色选择填充色
            //int color = dragonrise$getColorForBossEvent(bossEvent);
            //guiGraphics.fill(x + 2, y + SQUARE_SIZE - fillHeight, x + SQUARE_SIZE - 2, y + SQUARE_SIZE - 2, color);
            this.dragonrise$drawPieChart(guiGraphics, x, y, progress, bossEvent);
        }

        // 绘制边框（画4条线）
        guiGraphics.fill(x, y, x + SQUARE_SIZE, y + 1, 0xFFFFFFFF);
        guiGraphics.fill(x, y + SQUARE_SIZE - 1, x + SQUARE_SIZE, y + SQUARE_SIZE, 0xFFFFFFFF);
        guiGraphics.fill(x, y, x + 1, y + SQUARE_SIZE, 0xFFFFFFFF);
        guiGraphics.fill(x + SQUARE_SIZE - 1, y, x + SQUARE_SIZE, y + SQUARE_SIZE, 0xFFFFFFFF);

        poseStack.popPose();

        // 绘制BossBar名称（简写或首字母）
        String displayName = dragonrise$getAbbreviatedName(bossEvent.getName().getString());
        int textWidth = minecraft.font.width(displayName);
        int textX = x + (SQUARE_SIZE - textWidth) / 2;
        int textY = y + (SQUARE_SIZE - 8) / 2;

        guiGraphics.drawString(minecraft.font, displayName, textX, textY, 0xFFFFFF);
    }

    /**
     * 绘制饼图进度
     */
    @Unique
    private void dragonrise$drawPieChart(GuiGraphics guiGraphics, int x, int y, float progress, LerpingBossEvent bossEvent) {
        int color = dragonrise$getColorForBossEvent(bossEvent);
        int centerX = x + SQUARE_SIZE / 2;
        int centerY = y + SQUARE_SIZE / 2;

        //渲染第一象限

        if (progress > 0) {

            int fillProgress = (int) Math.min((SQUARE_SIZE * progress * 2), SQUARE_SIZE / 2.0f - 1);

            guiGraphics.fill(centerX, centerY, centerX + fillProgress, y + 1, color);
        }

        //渲染第二象限

        if (progress > 0.25) {

            int fillProgress = (int) Math.min((SQUARE_SIZE * (progress - 0.25) * 2), SQUARE_SIZE / 2.0f - 1);

            guiGraphics.fill(centerX, centerY, x+SQUARE_SIZE - 1, centerY + fillProgress, color);
        }
        //渲染第三象限
        if (progress > 0.5) {

            int fillProgress = (int) Math.min((SQUARE_SIZE * (progress - 0.5) * 2), SQUARE_SIZE / 2.0f - 1);

            guiGraphics.fill(centerX, centerY, centerX - fillProgress, y + SQUARE_SIZE - 1, color);
        }
        //渲染第四象限
        if (progress > 0.75) {

            int fillProgress = (int) Math.min((SQUARE_SIZE * (progress - 0.75) * 2), SQUARE_SIZE / 2.0f - 1);

            guiGraphics.fill(centerX, centerY, x + 1, centerY - fillProgress, color);
        }
    }

    /**
     * 根据Boss事件获取颜色
     */
    @Unique
    private int dragonrise$getColorForBossEvent(LerpingBossEvent bossEvent) {
        return switch (bossEvent.getColor()) {
            case PINK -> 0x99FF69B4;
            case BLUE -> 0x994169E1;
            case RED -> 0x99FF0000;
            case GREEN -> 0x9900FF00;
            case YELLOW -> 0x99FFFF00;
            case PURPLE -> 0x99800080;
            case WHITE -> 0x99FFFFFF;
            default -> 0x9900BFFF;
        };
    }

    /**
     * 获取简写的Boss名称
     */
    @Unique
    private String dragonrise$getAbbreviatedName(String fullName) {
        if (fullName.length() <= 3) return fullName;

        // 尝试提取首字母或缩写
        String[] words = fullName.split(" ");
        if (words.length > 1) {
            StringBuilder abbreviation = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    abbreviation.append(word.charAt(0));
                }
            }
            return abbreviation.toString();
        }

        // 如果只有一个单词，取前三个字符
        return fullName.substring(0, 3);
    }
}
