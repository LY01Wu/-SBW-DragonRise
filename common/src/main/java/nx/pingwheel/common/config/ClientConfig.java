package nx.pingwheel.common.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nx.pingwheel.common.network.UpdateChannelC2SPacket;
import nx.pingwheel.common.platform.IPlatformNetworkService;

import static nx.pingwheel.common.CommonClient.Game;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ClientConfig implements IConfig {
	int pingVolume = 100;
	int pingDuration = 7;
	int pingDistance = 2048;
	float correctionPeriod = 1f;
	boolean itemIconVisible = true;
	boolean directionIndicatorVisible = true;
	boolean nameLabelForced = false;
	int pingSize = 100;
	String channel = "";

	// hidden from the settings screen
	int removeRadius = 10;
	int raycastDistance = 1024;
	int safeZoneLeft = 5;
	int safeZoneRight = 5;
	int safeZoneTop = 5;
	int safeZoneBottom = 60;

	public static final int TPS = 20;
	public static final int MAX_PING_DURATION = 60;
	public static final int MAX_PING_DISTANCE = 2048;
	public static final float MAX_CORRECTION_PERIOD = 5f;
	public static final int MAX_CHANNEL_LENGTH = 128;

	public void validate() {
		if (channel.length() > MAX_CHANNEL_LENGTH) {
			channel = channel.substring(0, MAX_CHANNEL_LENGTH);
		}
	}

	public void onUpdate() {
		if (Game != null) {
			IPlatformNetworkService.INSTANCE.sendToServer(new UpdateChannelC2SPacket(channel));
		}
	}

	public static final ConfigHandler<ClientConfig> HANDLER = ConfigHandler.of(ClientConfig.class, ".json");
}
