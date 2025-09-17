package nx.pingwheel.forge.platform;

import com.atsuishio.superbwarfare.entity.vehicle.base.AircraftEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import nx.pingwheel.common.platform.IPlatformForgeService;

public class PlatformForgeServicempl implements IPlatformForgeService {

    //卓越最新版本会有直接获取Type
    @Override
    public int getVehicleType(Entity entity) {
        if(entity instanceof VehicleEntity vehicleEntity){
            if (vehicleEntity instanceof HelicopterEntity){
                return 0;
            }
            if (vehicleEntity instanceof LandArmorEntity){
                return 1;
            }
            if (vehicleEntity instanceof AircraftEntity){
                return 2;
            }
        }
        return -1;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
