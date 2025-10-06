package com.modernwarfare.dragonrise.client.layer;

import com.modernwarfare.dragonrise.entity.AbstractVehicleEntity;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class AbstractVehicleLayer extends GeoRenderLayer<AbstractVehicleEntity> {
    public AbstractVehicleLayer(GeoRenderer<AbstractVehicleEntity> entityRendererIn) {
        super(entityRendererIn);
    }
}
