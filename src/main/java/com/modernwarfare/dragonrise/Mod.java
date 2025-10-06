package com.modernwarfare.dragonrise;

import com.modernwarfare.dragonrise.config.Z10OBBconfig;
import com.modernwarfare.dragonrise.config.ServerConfig;
import com.modernwarfare.dragonrise.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessageFactory;
import org.apache.logging.log4j.message.Message;

@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
    public static final String MODID = "dragonrise";
    public static final String MOD_PREFIX = "[Dragon-Rise] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID,
            new FormattedMessageFactory() {
                @Override
                public Message newMessage(String message) {
                    return super.newMessage(MOD_PREFIX + message);
                }
            });

    public Mod() {
        Z10OBBconfig.HANDLER.load();
        for(int i =0;i<5;i++){
            Z10OBBconfig.HANDLERS[i].load();
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.init());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        DRModSounds.REGISTRY.register(bus);

        ModBlocks.REGISTRY.register(bus);

        ModBlockEntities.REGISTRY.register(bus);

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
