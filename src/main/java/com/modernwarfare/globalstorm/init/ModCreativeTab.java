package com.modernwarfare.globalstorm.init;

import com.modernwarfare.globalstorm.GlobalStorm;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GlobalStorm.MODID);

    public static void register(IEventBus eventBus) {
        CREATIVE_TAB.register(eventBus);
    }

    public static final RegistryObject<CreativeModeTab> GRENADE_TAB = CREATIVE_TAB.register("grenade_tab",
            () -> CreativeModeTab.builder()
                    .icon((() -> new ItemStack(ModItems.GRENADE.get())))
                    .title(Component.translatable("creative_tab.grenademod.grenade_tab"))
                    .displayItems((featureFlagSet, output) -> {
                        output.accept(new ItemStack(ModItems.GRENADE.get()));
                        //output.accept(new ItemStack(ModItems.STUN_GRENADE.get()));
                    })
                    .build());
}
