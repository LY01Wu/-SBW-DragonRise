package com.modernwarfare.globalstorm.init;

import com.modernwarfare.globalstorm.GlobalStorm;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlobalStorm.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> GRENADE = ITEMS.register("grenade",
            () -> new Item(new Item.Properties()));
//
//    public static final RegistryObject<Item> STUN_GRENADE = ITEMS.register("stun_grenade",
//            ()->new Item(new Item.Properties()));
}
