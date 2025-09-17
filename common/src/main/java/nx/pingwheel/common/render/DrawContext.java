package nx.pingwheel.common.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import nx.pingwheel.common.core.GameContext;
import nx.pingwheel.common.core.PingView;
import nx.pingwheel.common.math.MathUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static nx.pingwheel.common.CommonClient.Game;
import static nx.pingwheel.common.resource.ResourceConstants.*;
import static nx.pingwheel.common.resource.ResourceReloadListener.hasCustomTexture;

public class DrawContext {


	private static final int WHITE = FastColor.ARGB32.color(255, 255, 255, 255);
	private static final int SHADOW_BLACK = FastColor.ARGB32.color(64, 0, 0, 0);

	private GuiGraphics guiGraphics;
	@Getter
	private PoseStack matrices;

	public DrawContext(GuiGraphics guiGraphics) {
		this.guiGraphics = guiGraphics;
		this.matrices = guiGraphics.pose();
	}

	public void renderLabel(Component text, float yOffset, PlayerInfo player) {
		var extraWidth = (player != null) ? 10 : 0;
		var textMetrics = new Vec2(
			Game.font.width(text) + extraWidth,
			Game.font.lineHeight
		);
		var textOffset = textMetrics.scale(-0.5f).add(new Vec2(0f, textMetrics.y * yOffset));

		matrices.pushPose();
		matrices.translate(textOffset.x, textOffset.y, 0);
		guiGraphics.fill(-2, -2, (int)textMetrics.x + 1, (int)textMetrics.y, SHADOW_BLACK);
		guiGraphics.drawString(Game.font, text, extraWidth, 0, WHITE, false);

		if (player != null) {
			matrices.translate(-0.5, -0.5, 0);
			renderPlayerHead(player);
		}

		matrices.popPose();
	}

	public void renderPlayerHead(PlayerInfo player) {
		var texture = player.getSkinLocation();
		RenderSystem.enableBlend();
		guiGraphics.blit(texture, 0, 0, 0, 8, 8, 8, 8, 64, 64);
		guiGraphics.blit(texture, 0, 0, 0, 40, 8, 8, 8, 64, 64); // Overlay (hat)
		RenderSystem.disableBlend();
	}

	public void renderPing(ItemStack itemStack, boolean drawItemIcon, PingView ping){
		if (itemStack != null && drawItemIcon) {
			renderGuiItemModel(itemStack);
		} else if (hasCustomTexture()) {
			renderTexture(NONE_TEXTURE_ID, 16);
		}else if(ping.isDrawVehicleIcon()){
			switch (ping.getEntType()){
				case 0: renderTexture(HELI_TEXTURE_ID,16);break;
				case 1:	renderTexture(APC_TEXTURE_ID,16);break;
				case 2: renderTexture(JET_TEXTURE_ID,16);break;
			}
		}
		else {
			renderDefaultPingIcon();
		}
	}

	public void renderGuiItemModel(ItemStack itemStack) {
		guiGraphics.renderItem(itemStack, -8, -8, 0, -150);
	}

	public void renderDefaultPingIcon() {
		matrices.pushPose();
		MathUtils.rotateZ(matrices, (float)(Math.PI / 4f));
		matrices.translate(-2.5, -2.5, 0);
		guiGraphics.fill(0, 0, 5, 5, WHITE);
		matrices.popPose();
	}

	public void renderTexture(ResourceLocation texture, int size) {
		final var offset = size / -2;

		RenderSystem.enableBlend();
		guiGraphics.blit(
			texture,
			offset,
			offset,
			0,
			0,
			0,
			size,
			size,
			size,
			size
		);
		RenderSystem.disableBlend();
	}

	public void renderTexture(ResourceLocation texture, int sizew, int sizeh) {
		final var offsetw = sizew / -2;
		final var offseth = sizeh / -2;

		RenderSystem.enableBlend();
		guiGraphics.blit(
				texture,
				offsetw,
				offseth,
				0,
				0,
				0,
				sizew,
				sizeh,
				sizew,
				sizeh
		);
		RenderSystem.disableBlend();
	}

	public void renderArrowIcon() {
		renderTexture(ARROW_TEXTURE_ID, 10);
	}
}
