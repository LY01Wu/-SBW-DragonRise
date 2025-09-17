package com.modernwarfare.dragonrise.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;

public class ZTZ99AModel extends GeoModel<ZTZ99AEntity> {

    @Override
    public ResourceLocation getAnimationResource(ZTZ99AEntity entity) {
        return new ResourceLocation(Mod.MODID,"animations/ztz99a.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(ZTZ99AEntity entity) {
        return new ResourceLocation(Mod.MODID, "geo/ztz99a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZTZ99AEntity entity) {
        return new ResourceLocation(Mod.MODID, "textures/entity/ztz99a.png");
    }
}
