package com.modernwarfare.dragonrise.client.renderer.entity;

import com.modernwarfare.dragonrise.entity.AbstractVehicleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AbstractVehicleRenderer extends GeoEntityRenderer<AbstractVehicleEntity> {
    public AbstractVehicleRenderer(EntityRendererProvider.Context renderManager, GeoModel<AbstractVehicleEntity> model) {
        super(renderManager, model);
    }
}
