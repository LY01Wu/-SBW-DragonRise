package com.modernwarfare.globalstorm.events;

import com.modernwarfare.globalstorm.client.model.AmmoBoxEntityModel;
import com.modernwarfare.globalstorm.client.renderer.AmmoBoxEntityRenderer;
import com.modernwarfare.globalstorm.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.modernwarfare.globalstorm.GlobalStorm.MODID;
import static com.modernwarfare.globalstorm.init.ModModelLayer.AMMO_BOX_LAYER;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModEntities.AMMO_BOX.get(), AmmoBoxEntityRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        event.registerLayerDefinition(AMMO_BOX_LAYER, AmmoBoxEntityModel::createBodyLayer);

    }
}
