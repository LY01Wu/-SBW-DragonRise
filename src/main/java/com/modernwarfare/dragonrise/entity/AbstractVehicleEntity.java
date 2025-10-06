package com.modernwarfare.dragonrise.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.modernwarfare.dragonrise.config.AbstractVehicleConfig;
import com.modernwarfare.dragonrise.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class AbstractVehicleEntity extends ContainerMobileVehicleEntity implements GeoAnimatable {
    //数据包导入
//    private static final AbstractVehicleConfig VEHICLEDATA =
    //生成载具
//    public AbstractVehicleEntity(PlayMessages.SpawnEntity packet, Level world) {
//        this(ModEntities.VEHICLE.get(), world);
//    }
    //生成OBB
    public AbstractVehicleEntity(EntityType<AbstractVehicleEntity> type, Level world) {
        super(type, world);
    }

    //Geo的三个动画器
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
