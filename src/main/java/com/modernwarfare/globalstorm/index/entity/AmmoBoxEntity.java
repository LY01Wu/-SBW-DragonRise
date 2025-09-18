package com.modernwarfare.globalstorm.index.entity;

import com.modernwarfare.globalstorm.init.ModEntities;
import com.modernwarfare.globalstorm.resource.JsonAssetsManager;
import com.modernwarfare.globalstorm.resource.ammobox.AmmoBoxData;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmmoBoxEntity extends Entity implements OwnableEntity, Attackable {

    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(AmmoBoxEntity.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);

    public AmmoBoxEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.blocksBuilding = true;
        //this.noPhysics = false;
        //this.setNoGravity(false);
    }

    public AmmoBoxEntity(PlayMessages.SpawnEntity packet, Level level) {
        this(ModEntities.AMMO_BOX.get(), level);
        this.blocksBuilding = true;
        //this.noPhysics = false;
        //this.setNoGravity(false);
    }

    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEALTH, getMaxHealth());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Health")) {
            this.entityData.set(HEALTH, pCompound.getFloat("Health"));
        } else {
            this.entityData.set(HEALTH, getMaxHealth());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Health", this.entityData.get(HEALTH));
    }

    @Override
    public @NotNull InteractionResult interact(Player pPlayer, @NotNull InteractionHand pHand) {
        if (!this.level().isClientSide) {
            pPlayer.getCapability(ForgeCapabilities.ITEM_HANDLER, null).map(itemHandler -> {

                // 播放声音
                this.level().playSound(null, this.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.get(),
                        SoundSource.NEUTRAL, 1.0F, 1.0F);

                List<AmmoBoxData> ammoBoxData = new ArrayList<>();

                JsonAssetsManager.getINSTANCE().getAmmoBoxData().getAllData().forEach((key, value) -> {
                    ammoBoxData.addAll(value);
                });
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                    if (stackInSlot.getItem() instanceof IGun iGun) {
                        ResourceLocation gunId = iGun.getGunId(stackInSlot);
                        int currentAmmo = pPlayer.getCapability(ForgeCapabilities.ITEM_HANDLER, null).map(cap -> {
                            // 背包检查
                            int tmp = 0;
                            for (int j = 0; j < cap.getSlots(); j++) {
                                ItemStack checkAmmoStack = cap.getStackInSlot(j);
                                if (checkAmmoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stackInSlot, checkAmmoStack)) {
                                    pPlayer.sendSystemMessage(Component.literal(String.valueOf(checkAmmoStack.getCount())));
                                    tmp += checkAmmoStack.getCount();
                                }
                                if (checkAmmoStack.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(stackInSlot, checkAmmoStack)) {
                                    tmp += iAmmoBox.getAmmoCount(checkAmmoStack);
                                }
                            }
                            return tmp;
                        }).orElse(0);

                        //pPlayer.sendSystemMessage(Component.literal(value.toString()));
                        ammoBoxData.forEach(data -> {
                            pPlayer.sendSystemMessage(Component.literal(data.getId()+gunId.toString()));
                            if (data.getId().equals(gunId)) {
                                TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
                                    ResourceLocation ammoId = index.getGunData().getAmmoId();
                                    int count = data.getCount()-currentAmmo;
                                    if(count>0){
                                        ItemStack ammoItem = AmmoItemBuilder.create().setId(ammoId).setCount(count).build();
                                        ItemHandlerHelper.giveItemToPlayer(pPlayer, ammoItem);
                                    }
                                    pPlayer.sendSystemMessage(Component.literal(gunId + ammoId.toString()));
                                });
                            }
                        });


                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            });

            // 发送玩家信息
            pPlayer.sendSystemMessage(Component.literal("你的UUID是: " + pPlayer.getUUID().toString()));
            pPlayer.sendSystemMessage(Component.literal("你的名称是: " + pPlayer.getName().getString()));

        }

        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    public float getMaxHealth() {
        return 20.0F;
    }

    public float getHealth() {
        return this.entityData.get(HEALTH);
    }

    public void setHealth(float pHealth) {
        this.entityData.set(HEALTH, Mth.clamp(pHealth, 0.0F, getMaxHealth()));
    }

    @Override
    public void tick() {
        super.tick();
        // 确保super.tick()没有重置你的运动状态
        // 如果需要，可以在这里添加自定义的运动衰减逻辑
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9)); // 每tick减少10%的速度
        //if (!this.isNoGravity()) {
        this.move(MoverType.SELF, this.getDeltaMovement());
        //}

        this.pushEntities();
    }

    protected void pushEntities() {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::push);
        } else {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
//                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
//                if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
//                    int j = 0;
//
//                    for(int k = 0; k < list.size(); ++k) {
//                        if (!list.get(k).isPassenger()) {
//                            ++j;
//                        }
//                    }
//
//                    if (j > i - 1) {
//                        this.hurt(this.damageSources().cramming(), 6.0F);
//                    }
//                }

                for (int l = 0; l < list.size(); ++l) {
                    Entity entity = list.get(l);
                    this.push(entity);
                }
            }

        }
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }


    public boolean isPushable() {
        return true;
    }


    @Override
    public @Nullable UUID getOwnerUUID() {
        return null;
    }

    @Override
    public @Nullable LivingEntity getLastAttacker() {
        return null;
    }
}
