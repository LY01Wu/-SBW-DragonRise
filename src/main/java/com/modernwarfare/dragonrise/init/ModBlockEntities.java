package com.modernwarfare.dragonrise.init;


import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.block.blockEntity.SupplyStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Mod.MODID);

    public static final RegistryObject<BlockEntityType<SupplyStationBlockEntity>> SUPPLY_STATION = REGISTRY.register("supply_station",
            () -> BlockEntityType.Builder.of(SupplyStationBlockEntity::new, ModBlocks.SUPPLY_STATION.get()).build(null));

}
