package com.modernwarfare.globalstorm.events;


import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.modernwarfare.globalstorm.GlobalStorm.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        //event.put(ModEntities.AMMO_BOX.get(), AmmoBoxEntity.createAttributes().build());
    }

}
