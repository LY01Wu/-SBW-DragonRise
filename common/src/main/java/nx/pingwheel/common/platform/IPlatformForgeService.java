package nx.pingwheel.common.platform;

import net.minecraft.world.entity.Entity;

import java.util.ServiceLoader;

public interface IPlatformForgeService {
    IPlatformForgeService INSTANCE = ServiceLoader.load(IPlatformForgeService.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Forge no load"));
    int getVehicleType(Entity entity);
    boolean isModLoaded(String modId);
}
