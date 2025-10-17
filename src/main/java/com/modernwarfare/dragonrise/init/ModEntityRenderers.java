package com.modernwarfare.dragonrise.init;

import com.atsuishio.superbwarfare.client.renderer.entity.WgMissileRenderer;
import com.modernwarfare.dragonrise.client.renderer.entity.AKD9Renderer;
import com.modernwarfare.dragonrise.client.renderer.entity.ZHI10MERenderer;
import com.modernwarfare.dragonrise.client.renderer.entity.ZTZ99ARenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // Register entity render for tom7
        event.registerEntityRenderer(ModEntities.ZTZ99A.get(), ZTZ99ARenderer::new);
        event.registerEntityRenderer(ModEntities.ZHI10ME.get(), ZHI10MERenderer::new);
        event.registerEntityRenderer(ModEntities.AKD9.get(), AKD9Renderer::new);
    }
}

