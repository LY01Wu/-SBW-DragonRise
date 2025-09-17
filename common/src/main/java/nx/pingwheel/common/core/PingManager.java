package nx.pingwheel.common.core;

import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.network.PingLocationS2CPacket;
import nx.pingwheel.common.render.WorldRenderContext;

import java.util.ArrayList;
import java.util.Objects;

import static nx.pingwheel.common.CommonClient.Game;
import static nx.pingwheel.common.Global.LOGGER;
import static nx.pingwheel.common.config.ClientConfig.MAX_PING_DISTANCE;

public class PingManager {
	private PingManager() {}

	private static SoundQueueManager soundQueue;

	private static final ClientConfig CLIENT_CONFIG = ClientConfig.HANDLER.getConfig();

	public static final ArrayList<PingView> PING_REPO = new ArrayList<>();

	public static void clearPings() {
		PING_REPO.clear();
	}

	public static void addOrReplacePing(PingView newPing) {
		int index = -1;

		for (int i = 0; i < PING_REPO.size(); i++) {
			var entry = PING_REPO.get(i);

			if (Objects.equals(entry.authorId, newPing.authorId) && entry.sequence == newPing.sequence) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			PING_REPO.set(index, newPing);
		} else {
			PING_REPO.add(newPing);
		}
	}

	public static void updatePings(WorldRenderContext ctx) {
		if (Game.level == null || PING_REPO.isEmpty()) {
			return;
		}

		final var time = (int)Game.level.getGameTime();
		PingView target = null;

		for (var iter = PING_REPO.iterator(); iter.hasNext(); ) {
			final var ping = iter.next();

			ping.update(ctx, time);

			if (ping.isExpired()) {
				iter.remove();
			} else if (PingController.isPingQueued() && ping.isRemovable() && ping.isCloserToCenter(target)) {
				target = ping;
			}
		}

		if (target != null && PING_REPO.remove(target)) {
			PingController.revokePingAction();
		}

		PING_REPO.sort((a, b) -> Double.compare(b.getDistance(), a.getDistance()));
	}

	public static void acceptPingPacket(PingLocationS2CPacket packet) {
		//收包时更新声音队列

		if (packet.isCorrupt()) {
			LOGGER.warn("received invalid ping location from server");
			return;
		}

		final var connection = Game.getConnection();

		if (Game.player == null || Game.level == null || connection == null) {
			return;
		}

		if (!packet.channel().equals(CLIENT_CONFIG.getChannel())) {
			return;
		}

		if (CLIENT_CONFIG.getPingDistance() < MAX_PING_DISTANCE) {
			var vecToPing = Game.player.position().vectorTo(packet.pos());

			if (vecToPing.length() > CLIENT_CONFIG.getPingDistance()) {
				return;
			}
		}

		Game.execute(() -> {
			final var newPing = PingView.from(packet);

			PingManager.addOrReplacePing(newPing);
			newPing.playSound(soundQueue);
		});
	}

}
