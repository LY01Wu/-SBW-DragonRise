package com.modernwarfare.dragonrise.client.model;

import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.ZHI10MEEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ZHI10MEModel extends GeoModel<ZHI10MEEntity> {
    @Override
    public ResourceLocation getModelResource(ZHI10MEEntity zhi10MEEntity) {
        return new ResourceLocation(Mod.MODID, "geo/zhi10me.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZHI10MEEntity zhi10MEEntity) {
        return new ResourceLocation(Mod.MODID, "textures/entity/zhi10me.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ZHI10MEEntity zhi10MEEntity) {
        return null;
    }
}
