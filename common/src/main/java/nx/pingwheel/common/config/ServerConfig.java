package nx.pingwheel.common.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nx.pingwheel.common.core.ServerCore;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ServerConfig implements IConfig {
	ChannelMode defaultChannelMode = ChannelMode.AUTO;
	boolean playerTrackingEnabled = true;
	// 单次标记热量;每刻基础冷却量;过热热量
	int onePointHeat=3;
	float oneTickCoolant=0.05f;
	int heatMax=10;

	public void validate() {
	}

	public void onUpdate() {
		ServerCore.init();
	}

	public static final ConfigHandler<ServerConfig> HANDLER = ConfigHandler.of(ServerConfig.class, ".server.json");
}
