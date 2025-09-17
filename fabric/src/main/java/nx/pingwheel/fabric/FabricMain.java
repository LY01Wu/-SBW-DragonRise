package nx.pingwheel.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import nx.pingwheel.common.CommonServer;
import nx.pingwheel.common.command.ServerCommandBuilder;
import nx.pingwheel.common.network.PingLocationC2SPacket;
import nx.pingwheel.common.network.UpdateChannelC2SPacket;
import nx.pingwheel.common.resource.LanguageUtils;

public class FabricMain implements ModInitializer {

	@Override
	public void onInitialize() {
		CommonServer.INSTANCE.onInit();

		ServerPlayNetworking.registerGlobalReceiver(
			PingLocationC2SPacket.PACKET_ID,
			(server, player, a, packet, b)
				-> CommonServer.INSTANCE.onPingLocationPacket(server, player, PingLocationC2SPacket.readSafe(packet))
		);
		ServerPlayNetworking.registerGlobalReceiver(
			UpdateChannelC2SPacket.PACKET_ID,
			(server, player, a, packet, b)
				-> CommonServer.INSTANCE.onChannelUpdatePacket(server, player, UpdateChannelC2SPacket.readSafe(packet))
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(ServerCommandBuilder.build((context, success, response) -> {
				if (success) {
					context.getSource().sendSuccess(() -> LanguageUtils.withModPrefix(response), false);
				} else {
					context.getSource().sendFailure(LanguageUtils.withModPrefix(response));
				}
			}));
		});
	}
}
