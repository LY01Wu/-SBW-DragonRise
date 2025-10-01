package com.modernwarfare.dragonrise.block.blockEntity;

import com.modernwarfare.dragonrise.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SupplyStationBlockEntity extends BlockEntity {

    public SupplyStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUPPLY_STATION.get(), pPos, pBlockState);
    }

}
