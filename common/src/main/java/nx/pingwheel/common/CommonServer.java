package nx.pingwheel.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import nx.pingwheel.common.config.ServerConfig;
import nx.pingwheel.common.core.ServerCore;
import nx.pingwheel.common.network.PingLocationC2SPacket;
import nx.pingwheel.common.network.UpdateChannelC2SPacket;
import nx.pingwheel.common.platform.IPlatformServerEventService;

import static nx.pingwheel.common.Global.LOGGER;

public class CommonServer {

	public static final CommonServer INSTANCE = new CommonServer();
	private CommonServer() {}

	public void onInit() {
		LOGGER.info("Init");

		ServerConfig.HANDLER.load();

		ServerCore.init();

		IPlatformServerEventService.INSTANCE.registerPlayerLogoutEvent(this::onPlayerDisconnect);
	}

	public void onPingLocationPacket(MinecraftServer server, ServerPlayer player, PingLocationC2SPacket packet) {
		ServerCore.onPingLocation(server, player, packet);
	}

	public void onChannelUpdatePacket(MinecraftServer server, ServerPlayer player, UpdateChannelC2SPacket packet) {
		ServerCore.onChannelUpdate(player, packet);
	}

	public void onPlayerDisconnect(ServerPlayer player) {
		ServerCore.onPlayerDisconnect(player);
	}
}
