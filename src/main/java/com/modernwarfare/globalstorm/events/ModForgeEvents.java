package com.modernwarfare.globalstorm.events;

import com.modernwarfare.globalstorm.GlobalStorm;
import com.modernwarfare.globalstorm.index.command.ModCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlobalStorm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {

        ModCommands.register(event.getDispatcher());
    }

}
