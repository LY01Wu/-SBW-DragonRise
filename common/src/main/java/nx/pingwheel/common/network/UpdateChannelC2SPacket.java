package nx.pingwheel.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static nx.pingwheel.common.Global.MOD_ID;
import static nx.pingwheel.common.config.ClientConfig.MAX_CHANNEL_LENGTH;

public record UpdateChannelC2SPacket(String channel) implements IPacket {

	public static final ResourceLocation PACKET_ID = new ResourceLocation(MOD_ID + "-c2s", "update-channel");

	public UpdateChannelC2SPacket() {
		this((String)null);
	}

	public UpdateChannelC2SPacket(FriendlyByteBuf buf) {
		this(buf.readUtf(MAX_CHANNEL_LENGTH));
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(channel, MAX_CHANNEL_LENGTH);
	}

	public boolean isCorrupt() {
		return channel == null;
	}

	public ResourceLocation getId() {
		return PACKET_ID;
	}

	public static UpdateChannelC2SPacket readSafe(FriendlyByteBuf buf) {
		return PacketHandler.readSafe(buf, UpdateChannelC2SPacket.class);
	}
}
