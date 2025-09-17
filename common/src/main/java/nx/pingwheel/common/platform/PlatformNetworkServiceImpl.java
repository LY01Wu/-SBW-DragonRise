package nx.pingwheel.common.platform;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import nx.pingwheel.common.network.IPacket;

import static nx.pingwheel.common.CommonClient.Game;

public class PlatformNetworkServiceImpl implements IPlatformNetworkService {

	@Override
	public void sendToServer(IPacket packet) {
		var connection = Game.getConnection();

		if (connection == null) {
			return;
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.write(buf);

		connection.send(new ServerboundCustomPayloadPacket(packet.getId(), buf));
	}

	@Override
	public void sendToClient(IPacket packet, ServerPlayer player) {
		var buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.write(buf);

		player.connection.send(new ClientboundCustomPayloadPacket(packet.getId(), buf));
	}
}
