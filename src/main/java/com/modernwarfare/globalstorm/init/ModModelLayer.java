package com.modernwarfare.globalstorm.init;

import com.google.common.collect.Sets;
import com.modernwarfare.globalstorm.client.model.AmmoBoxEntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

import static com.modernwarfare.globalstorm.GlobalStorm.MODID;

public class ModModelLayer {

    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();

    public static final ModelLayerLocation AMMO_BOX_LAYER = register("ammo_box");


    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171296_, String p_171297_) {
        ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
        if (!ALL_MODELS.add(modellayerlocation)) {
            throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
        } else {
            return modellayerlocation;
        }
    }

    private static ModelLayerLocation createLocation(String path, String p_171302_) {
        return new ModelLayerLocation(new ResourceLocation(MODID, path), p_171302_);
    }
}
