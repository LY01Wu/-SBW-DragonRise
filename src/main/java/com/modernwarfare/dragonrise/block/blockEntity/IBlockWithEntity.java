package com.modernwarfare.dragonrise.block.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockWithEntity<T extends BlockEntity> extends EntityBlock {

    BlockEntityType<? extends T> getBlockEntityType();

    @Override
    default BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return getBlockEntityType().create(pPos, pState);
    }
}
