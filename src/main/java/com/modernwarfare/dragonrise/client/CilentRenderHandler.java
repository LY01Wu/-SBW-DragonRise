package com.modernwarfare.dragonrise.client;

import com.atsuishio.superbwarfare.Mod;
import com.modernwarfare.dragonrise.client.overlay.Z10MEOverlay;
import com.modernwarfare.dragonrise.client.overlay.ZTZ99AMgHudOverlay;
import com.modernwarfare.dragonrise.client.renderer.entity.ZTZ99ARenderer;
import com.modernwarfare.dragonrise.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CilentRenderHandler {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll(Z10MEOverlay.ID,new Z10MEOverlay());
    }
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ZTZ99A.get(), ZTZ99ARenderer::new);
    }
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(ZTZ99AMgHudOverlay.ID, new ZTZ99AMgHudOverlay());
    }
}