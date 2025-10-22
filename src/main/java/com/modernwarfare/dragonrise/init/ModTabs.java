package com.modernwarfare.dragonrise.init;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.common.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.modernwarfare.dragonrise.Mod;

@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Mod.MODID);

    public static final RegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("dragonrise",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.dragonrise.title"))
                    .icon(() -> new ItemStack(ModItems.CONTAINER.get()))
                    .displayItems((param, output) -> {
                                //output.accept(ContainerBlockItem.createInstance(ModEntities.TOM_7.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.ZTZ99A.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.ZBD04A.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.ZHI10ME.get()));

                            }
                    )
                    .build());
}
