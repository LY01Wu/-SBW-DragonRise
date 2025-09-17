package com.modernwarfare.dragonrise.init;

import com.modernwarfare.dragonrise.client.renderer.entity.ZHI10MERenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.modernwarfare.dragonrise.client.renderer.entity.ZTZ99ARenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // Register entity render for tom7
        //event.registerEntityRenderer(ModEntities.TOM_7.get(), Tom7Renderer::new);
        event.registerEntityRenderer(ModEntities.ZTZ99A.get(), ZTZ99ARenderer::new);
        event.registerEntityRenderer(ModEntities.ZHI10ME.get(), ZHI10MERenderer::new);
    }
}

