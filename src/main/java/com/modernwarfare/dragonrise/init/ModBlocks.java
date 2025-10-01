package com.modernwarfare.dragonrise.init;


import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.block.SupplyStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Mod.MODID);

    public static final RegistryObject<Block> SUPPLY_STATION = REGISTRY.register("supply_station", SupplyStationBlock::new);
}
