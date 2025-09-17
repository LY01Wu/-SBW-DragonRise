package nx.pingwheel.common.render;

import net.minecraft.world.phys.Vec2;
import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.core.PingView;
import nx.pingwheel.common.math.MathUtils;

import java.lang.reflect.InvocationTargetException;

import static nx.pingwheel.common.CommonClient.Game;

public class DirectionIndicatorRenderer {
	private DirectionIndicatorRenderer() {}

	private static final ClientConfig CLIENT_CONFIG = ClientConfig.HANDLER.getConfig();

	private static Vec2 screenSize;
	private static Vec2 safeZoneTopLeft;
	private static Vec2 safeZoneBottomRight;
	private static Vec2 safeScreenCenter;

	public static void prepareSafeZone() {
		final var wnd = Game.getWindow();

		screenSize = new Vec2(wnd.getGuiScaledWidth(), wnd.getGuiScaledHeight());
		safeZoneTopLeft = new Vec2(CLIENT_CONFIG.getSafeZoneLeft(), CLIENT_CONFIG.getSafeZoneTop());
		safeZoneBottomRight = new Vec2(screenSize.x - CLIENT_CONFIG.getSafeZoneRight(), screenSize.y - CLIENT_CONFIG.getSafeZoneBottom());
		safeScreenCenter = new Vec2((safeZoneBottomRight.x - safeZoneTopLeft.x) * 0.5f, (safeZoneBottomRight.y - safeZoneTopLeft.y) * 0.5f);
	}

	public static void draw(DrawContext ctx, PingView ping){
		final var screenPos = ping.getScreenPos();

		if (screenPos == null) {
			return;
		}

		final var behindCamera = screenPos.isBehindCamera();
		final var isOffScreen = behindCamera || !screenPos.isInBounds(Vec2.ZERO, screenSize);

		if (!isOffScreen) {
			return;
		}

		var pingDirectionVec = new Vec2(screenPos.x - safeZoneTopLeft.x - safeScreenCenter.x, screenPos.y - safeZoneTopLeft.y - safeScreenCenter.y);

		if (behindCamera) {
			pingDirectionVec = pingDirectionVec.scale(-1);
		}

		final var m = ctx.getMatrices();
		final var pingScale = ping.getScale();
		final var pingSize = CLIENT_CONFIG.getPingSize() / 100f;
		final var pingAngle = (float)Math.atan2(pingDirectionVec.y, pingDirectionVec.x);
		final var edgePosition = MathUtils.calculateAngleRectIntersection(pingAngle, safeZoneTopLeft, safeZoneBottomRight);
		final var indicatorOffsetX = Math.cos(pingAngle + Math.PI) * 12;
		final var indicatorOffsetY = Math.sin(pingAngle + Math.PI) * 12;

		m.pushPose();
		{
			m.translate(edgePosition.x, edgePosition.y, 0f);

			m.pushPose();
			{
				m.scale(pingScale, pingScale, 1f);
				m.translate(indicatorOffsetX, indicatorOffsetY, 0);
				ctx.renderPing(ping.getItemStack(), CLIENT_CONFIG.isItemIconVisible(),ping);
			}
			m.popPose();

			m.pushPose();
			{
				MathUtils.rotateZ(m, pingAngle);
				m.scale(pingSize, pingSize, 1f);

				m.scale(0.25f, 0.25f, 1f);
				m.translate(-5f, 0f, 0f);
				ctx.renderArrowIcon();
			}
			m.popPose();
		}
		m.popPose();
	}
}
