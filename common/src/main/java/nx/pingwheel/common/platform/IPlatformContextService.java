package nx.pingwheel.common.platform;

import net.minecraft.client.KeyMapping;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface IPlatformContextService {

	IPlatformContextService INSTANCE = ServiceLoader.load(IPlatformContextService.class)
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("No IPlatformContextService implementation found!"));

	String getSelfModVersion();
	Path resolveConfigDir(String path);
	void registerKeyMapping(KeyMapping keyMapping);
	boolean isModLoaded(String modId);
}
