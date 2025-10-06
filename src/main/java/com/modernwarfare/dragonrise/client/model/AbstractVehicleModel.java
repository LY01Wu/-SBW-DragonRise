package com.modernwarfare.dragonrise.client.model;

import com.modernwarfare.dragonrise.entity.AbstractVehicleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbstractVehicleModel extends GeoModel<AbstractVehicleEntity> {
    @Override
    public ResourceLocation getModelResource(AbstractVehicleEntity animatable) {
        return null;
    }

    @Override
    public ResourceLocation getTextureResource(AbstractVehicleEntity animatable) {
        return null;
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractVehicleEntity animatable) {
        return null;
    }
}
