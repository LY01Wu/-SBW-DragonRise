package com.modernwarfare.dragonrise.client.model;

import com.atsuishio.superbwarfare.Mod;
import com.modernwarfare.dragonrise.entity.AKD9Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AKD9Model extends GeoModel<AKD9Entity> {

    @Override
    public ResourceLocation getModelResource(AKD9Entity animatable) {
        return Mod.loc("geo/wg_missile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AKD9Entity animatable) {

        return Mod.loc("textures/entity/javelin_missile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AKD9Entity animatable) {
        return Mod.loc("animations/javelin_missile.animation.json");

    }
}
