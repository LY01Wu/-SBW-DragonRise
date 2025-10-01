package com.modernwarfare.dragonrise.block.blockEntity;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SupplyStationBlockEntity extends BlockEntity {

    public static final int CHARGE_RADIUS = 8;

    public SupplyStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUPPLY_STATION.get(), pPos, pBlockState);
    }

    public static void serverTick(SupplyStationBlockEntity blockEntity) {
        if (blockEntity.level == null) return;
        List<Entity> entities = blockEntity.level.getEntitiesOfClass(Entity.class, new AABB(blockEntity.getBlockPos()).inflate(CHARGE_RADIUS));
        entities.forEach(entity -> {
            if (entity instanceof WeaponVehicleEntity vehicle && entity instanceof VehicleEntity) {
                blockEntity.refillAmmo(vehicle, (VehicleEntity) entity);
            }
        });
    }

    private void refillAmmo(WeaponVehicleEntity vehicle, VehicleEntity pEntity) {
        VehicleWeapon[][] weapons = vehicle.getAllWeapons();
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] != null) {
                for (int j = 0; j < weapons[i].length; j++) {
                    if (weapons[i][j] != null) {
                        //Mod.LOGGER.info(weapons[i][j].maxAmmo);
                        pEntity.insertItem(weapons[i][j].ammo, weapons[i][j].maxAmmo - weapons[i][j].currentAmmo);
                        //weapons[i][j].currentAmmo = weapons[i][j].maxAmmo;
                    }
                }
            }
        }
    }

}
