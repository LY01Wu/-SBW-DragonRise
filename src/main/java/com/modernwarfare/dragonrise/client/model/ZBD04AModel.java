package com.modernwarfare.dragonrise.client.model;

import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.ZBD04AEntity;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ZBD04AModel extends GeoModel<ZBD04AEntity> {

    @Override
    public ResourceLocation getAnimationResource(ZBD04AEntity entity) {
        return new ResourceLocation(Mod.MODID,"animations/zbd04a.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(ZBD04AEntity entity) {
        return new ResourceLocation(Mod.MODID, "geo/zbd04a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZBD04AEntity entity) {
        return new ResourceLocation(Mod.MODID, "textures/entity/zbd04a.png");
    }
}
