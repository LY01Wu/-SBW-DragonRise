package nx.pingwheel.forge.platform;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import nx.pingwheel.common.platform.IPlatformContextService;

import java.nio.file.Path;

import static nx.pingwheel.forge.ForgeMain.FORGE_ID;

public class PlatformContextServiceImpl implements IPlatformContextService {

	@Override
	public String getSelfModVersion() {
		return ModList.get().getModContainerById(FORGE_ID)
			.map(container -> container.getModInfo().getVersion().toString())
			.orElse("Unknown");
	}

	@Override
	public Path resolveConfigDir(String path) {
		return FMLPaths.CONFIGDIR.get().resolve(path);
	}

	@Override
	public void registerKeyMapping(KeyMapping keyMapping) {
		FMLJavaModLoadingContext
			.get()
			.getModEventBus()
			.addListener((RegisterKeyMappingsEvent event) -> event.register(keyMapping));
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}
}
