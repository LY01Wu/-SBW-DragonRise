package nx.pingwheel.common.render;

import net.minecraft.client.gui.GuiGraphics;
import nx.pingwheel.common.core.GameContext;
import nx.pingwheel.common.core.PingManager;
import nx.pingwheel.common.config.ClientConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static nx.pingwheel.common.CommonClient.Game;

public class OverlayRenderer {
	private OverlayRenderer() {}

	private static final ClientConfig CLIENT_CONFIG = ClientConfig.HANDLER.getConfig();
	public static void draw(GuiGraphics guiGraphics, float tickDelta){
		final var pingRepo = PingManager.PING_REPO;

		if (Game.player == null || pingRepo.isEmpty() ) {
			return;
		}

		final var m = guiGraphics.pose();
		final var ctx = new DrawContext(guiGraphics);
		final var showDirectionIndicator = CLIENT_CONFIG.isDirectionIndicatorVisible();

		if (showDirectionIndicator) {
			DirectionIndicatorRenderer.prepareSafeZone();
		}

		m.pushPose();
		m.translate(0f, 0f, -pingRepo.size() * 16f);

		for (var ping : pingRepo) {
            final var screenPos = ping.getScreenPos();

			if (screenPos == null
			||  ping.dimension != GameContext.getDimension()
			||  !Objects.equals(ping.getGetRenderPlayer().getTeam(),
				Objects.requireNonNull(GameContext.getEntity(ping.authorId)).getTeam()
				)
			){
				continue;
			}

			final var behindCamera = screenPos.isBehindCamera();

			if (behindCamera && !showDirectionIndicator) {
				continue;
			}

			m.translate(0f, 0f, 16f);

			if (showDirectionIndicator) {
				DirectionIndicatorRenderer.draw(ctx, ping);
			}

			if (!behindCamera) {
				PingLocationRenderer.draw(ctx, ping);
			}
		}

		m.popPose();
	}
}
