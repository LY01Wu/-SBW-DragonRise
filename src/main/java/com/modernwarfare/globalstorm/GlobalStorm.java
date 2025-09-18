package com.modernwarfare.globalstorm;

import com.modernwarfare.globalstorm.init.ModCreativeTab;
import com.modernwarfare.globalstorm.init.ModEntities;
import com.modernwarfare.globalstorm.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(GlobalStorm.MODID)
public class GlobalStorm {

    public static final String MODID = "globalstorm";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public GlobalStorm() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.REGISTRY.register(modEventBus);

        ModItems.register(modEventBus);

        ModCreativeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

}
