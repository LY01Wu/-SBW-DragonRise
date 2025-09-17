package com.modernwarfare.dragonrise;

import com.modernwarfare.dragonrise.config.ServerConfig;
import com.modernwarfare.dragonrise.init.DRModSounds;
import com.modernwarfare.dragonrise.init.ModEntities;
import com.modernwarfare.dragonrise.init.ModTabs;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
    public static final String MODID = "dragonrise";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Mod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.init());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        DRModSounds.REGISTRY.register(bus);

        ModEntities.REGISTRY.register(bus);

        ModTabs.TABS.register(bus);

        bus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("common setup");
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
