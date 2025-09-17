package nx.pingwheel.common.core;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import nx.pingwheel.common.config.ServerConfig;
import nx.pingwheel.common.config.ChannelMode;
import nx.pingwheel.common.util.RateLimiter;
import nx.pingwheel.common.platform.IPlatformNetworkService;
import nx.pingwheel.common.network.PingLocationC2SPacket;
import nx.pingwheel.common.network.PingLocationS2CPacket;
import nx.pingwheel.common.network.UpdateChannelC2SPacket;

import java.util.HashMap;
import java.util.UUID;

import static nx.pingwheel.common.Global.*;

public class ServerCore {
	private ServerCore() {}

	private static final ServerConfig SERVER_CONFIG = ServerConfig.HANDLER.getConfig();
	private static final HashMap<UUID, String> PLAYER_CHANNELS = new HashMap<>();
	private static final HashMap<UUID, RateLimiter> PLAYER_RATES = new HashMap<>();

	public static void init() {
		//从SERVER_CONFIG中读取配置信息
		RateLimiter.setRates(SERVER_CONFIG.getOnePointHeat(),SERVER_CONFIG.getOneTickCoolant(),SERVER_CONFIG.getHeatMax());
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		PLAYER_CHANNELS.remove(player.getUUID());
		PLAYER_RATES.remove(player.getUUID());
	}

	public static void onChannelUpdate(ServerPlayer player, UpdateChannelC2SPacket packet) {
		if (packet.isCorrupt()) {
			LOGGER.warn(() -> "invalid channel update from %s (%s)".formatted(player.getGameProfile().getName(), player.getUUID()));
			player.displayClientMessage(Component.literal("§8[Ping-Wheel] §cChannel couldn't be updated\n§fMake sure your version matches the server's version: §d" + MOD_VERSION), false);
			return;
		}

		updatePlayerChannel(player, packet.channel());
	}

	public static void onPingLocation(MinecraftServer server, ServerPlayer player, PingLocationC2SPacket packet) {
		if (packet.isCorrupt()) {
			LOGGER.warn(() -> "invalid ping location from %s (%s)".formatted(player.getGameProfile().getName(), player.getUUID()));
			player.displayClientMessage(Component.literal("§8[Ping-Wheel] §cUnable to send ping\n§fMake sure your version matches the server's version: §d" + MOD_VERSION), false);
			return;
		}

		PLAYER_RATES.putIfAbsent(player.getUUID(), new RateLimiter());
		final var rateLimiter = PLAYER_RATES.get(player.getUUID());

		if (rateLimiter.checkExceeded()) {
			return;
		}
		
		final var channel = packet.channel();
		final var defaultChannelMode = SERVER_CONFIG.getDefaultChannelMode();

		if (channel.isEmpty() && defaultChannelMode == ChannelMode.DISABLED) {
			player.displayClientMessage(Component.literal("§8[Ping-Wheel] §eMust be in a channel to ping location\n§fUse §a/pingwheel channel§f to switch"), false);
			return;
		}

		if (channel.isEmpty() && defaultChannelMode == ChannelMode.TEAM_ONLY && player.getTeam() == null) {
			player.displayClientMessage(Component.literal("§8[Ping-Wheel] §eMust be in a team or channel to ping location\n§fUse §a/pingwheel channel§f to switch"), false);
			return;
		}

		if (!channel.equals(PLAYER_CHANNELS.getOrDefault(player.getUUID(), ""))) {
			updatePlayerChannel(player, channel);
		}

		PingLocationS2CPacket packetOut;
		final var playerList = server.getPlayerList();

		if (!SERVER_CONFIG.isPlayerTrackingEnabled() && targetEntityIsPlayer(packet, playerList)) {
			packetOut = new PingLocationS2CPacket(packet.channel(), packet.pos(), null, packet.sequence(), packet.dimension(), player.getUUID());
		} else {
			packetOut = PingLocationS2CPacket.fromClientPacket(packet, player.getUUID());
		}

		for (ServerPlayer p : playerList.getPlayers()) {
			if (!channel.equals(PLAYER_CHANNELS.getOrDefault(p.getUUID(), ""))) {
				continue;
			}

			if (defaultChannelMode != ChannelMode.GLOBAL && player.getTeam() != p.getTeam()) {
				continue;
			}

			IPlatformNetworkService.INSTANCE.sendToClient(packetOut, p);
		}
	}

	private static boolean targetEntityIsPlayer(PingLocationC2SPacket packet, PlayerList playerList) {
		final var playerUUID = packet.entity();

		if (playerUUID == null) {
			return false;
		}

		return playerList.getPlayer(playerUUID) != null;
	}

	private static void updatePlayerChannel(ServerPlayer player, String channel) {
		if (channel.isEmpty()) {
			PLAYER_CHANNELS.remove(player.getUUID());
			LOGGER.info(() -> "Channel update: %s -> default".formatted(player.getGameProfile().getName()));
		} else {
			PLAYER_CHANNELS.put(player.getUUID(), channel);
			LOGGER.info(() -> "Channel update: %s -> \"%s\"".formatted(player.getGameProfile().getName(), channel));
		}
	}
}
