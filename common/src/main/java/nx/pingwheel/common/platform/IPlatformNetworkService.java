package nx.pingwheel.common.platform;

import net.minecraft.server.level.ServerPlayer;
import nx.pingwheel.common.network.IPacket;

import java.util.ServiceLoader;

public interface IPlatformNetworkService {

	IPlatformNetworkService INSTANCE = ServiceLoader.load(IPlatformNetworkService.class)
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("No IPlatformNetworkService implementation found!"));

	void sendToServer(IPacket packet);
	void sendToClient(IPacket packet, ServerPlayer player);
}
