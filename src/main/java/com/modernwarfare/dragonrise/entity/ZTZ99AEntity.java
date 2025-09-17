package com.modernwarfare.dragonrise.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmokeDecoyEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.modernwarfare.dragonrise.config.server.DragonRiseServerConfig;
import com.modernwarfare.dragonrise.init.DRModSounds;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.modernwarfare.dragonrise.init.ModEntities;

import java.util.List;

import static com.atsuishio.superbwarfare.client.RenderHelper.blit;
import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class ZTZ99AEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_AP = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_HE = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_AMMO_TYPE = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RELOAD_COOLDOWN = SynchedEntityData.defineId(ZTZ99AEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public OBB obbBody1;
    public OBB obbBody2;
    public OBB obbWheelLeft;
    public OBB obbWheelRight;
    public OBB obbEngine;
    public OBB obbTurret;
    public OBB obbTurret2;

    public ZTZ99AEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ZTZ99A.get(), world);
    }

    public ZTZ99AEntity(EntityType<ZTZ99AEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.obbBody1 = new OBB(this.position().toVector3f(), new Vector3f(1.96875f, 0.54f, 4.5f), new Quaternionf(), OBB.Part.BODY);
        this.obbBody2 = new OBB(this.position().toVector3f(), new Vector3f(1.96875f, 0.09375f, 2.7421875f), new Quaternionf(), OBB.Part.BODY);
        this.obbWheelLeft = new OBB(this.position().toVector3f(), new Vector3f(0.3375f, 0.6328f, 4.21875f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obbWheelRight = new OBB(this.position().toVector3f(), new Vector3f(0.3375f, 0.6328f, 4.21875f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obbEngine = new OBB(this.position().toVector3f(), new Vector3f(1.875f, 0.5625f, 1.4f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(1.471875f, 0.515625f, 1.64f), new Quaternionf(), OBB.Part.TURRET);
        this.obbTurret2 = new OBB(this.position().toVector3f(), new Vector3f(1.3125f, 0.38f, 0.4453125f), new Quaternionf(), OBB.Part.TURRET);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        // AP
                        new CannonShellWeapon()
                                .hitDamage(DragonRiseServerConfig.ZTZ99A_AP_CANNON_DAMAGE.get())
                                .explosionRadius(DragonRiseServerConfig.ZTZ99A_AP_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(DragonRiseServerConfig.ZTZ99A_AP_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0)
                                .fireTime(0)
                                .durability(100)
                                .velocity(40)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_MISSILE.get())
                                .ammo(ModItems.AP_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png"))
                                .sound1p(DRModSounds.ZTZ99A_FIRE_1P.get())
                                .sound3p(DRModSounds.ZTZ99A_FIRE_3P.get())
                                .sound3pFar(ModSounds.YX_100_FAR.get())
                                .sound3pVeryFar(ModSounds.YX_100_VERYFAR.get()),
                        // HE
                        new CannonShellWeapon()
                                .hitDamage(DragonRiseServerConfig.ZTZ99A_HE_CANNON_DAMAGE.get())
                                .explosionRadius(DragonRiseServerConfig.ZTZ99A_HE_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(DragonRiseServerConfig.ZTZ99A_HE_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0.18F)
                                .fireTime(2)
                                .durability(1)
                                .velocity(25)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_CANNON.get())
                                .ammo(ModItems.HE_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png"))
                                .sound1p(DRModSounds.ZTZ99A_FIRE_1P.get())
                                .sound3p(DRModSounds.ZTZ99A_FIRE_3P.get())
                                .sound3pFar(ModSounds.YX_100_FAR.get())
                                .sound3pVeryFar(ModSounds.YX_100_VERYFAR.get()),
                        // 同轴重机枪
                        new ProjectileWeapon()
                                .damage(VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                                .headShot(2)
                                .zoom(false)
                                .bypassArmorRate(0.4f)
                                .ammo(ModItems.HEAVY_AMMO.get())
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                                .sound1p(ModSounds.M_2_HB_FIRE_1P.get())
                                .sound3p(ModSounds.M_2_HB_FIRE_3P.get())
                                .sound3pFar(ModSounds.M_2_HB_FAR.get())
                                .sound3pVeryFar(ModSounds.M_2_HB_VERYFAR.get()),
                },
                new VehicleWeapon[]{
                        // 机枪
                        new ProjectileWeapon()
                                .damage(VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                                .headShot(2)
                                .zoom(false)
                                .bypassArmorRate(0.4f)
                                .ammo(ModItems.HEAVY_AMMO.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                                .sound1p(ModSounds.M_2_HB_FIRE_1P.get())
                                .sound3p(ModSounds.M_2_HB_FIRE_3P.get())
                                .sound3pFar(ModSounds.M_2_HB_FAR.get())
                                .sound3pVeryFar(ModSounds.M_2_HB_VERYFAR.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return switch (index) {
            case 0 -> new ThirdPersonCameraPosition(4 + ClientMouseHandler.custom3pDistanceLerp, 1.5, -0.8669625);
            case 1 -> new ThirdPersonCameraPosition(4 + 0.5 * ClientMouseHandler.custom3pDistanceLerp, 1.5, 0);
            case 2 -> new ThirdPersonCameraPosition(5 + 0.5 * ClientMouseHandler.custom3pDistanceLerp, 1.5, 0);
            default -> null;
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(LOADED_AP, 0);
        this.entityData.define(LOADED_HE, 0);
        this.entityData.define(LOADED_AMMO_TYPE, 0);
        this.entityData.define(GUN_FIRE_TIME, 0);
        this.entityData.define(RELOAD_COOLDOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedAP", this.entityData.get(LOADED_AP));
        compound.putInt("LoadedHE", this.entityData.get(LOADED_HE));
        compound.putInt("LoadedAmmoType", this.entityData.get(LOADED_AMMO_TYPE));
        compound.putInt("WeaponType", getWeaponIndex(0));
        compound.putInt("PassengerWeaponType", getWeaponIndex(1));
        compound.putInt("ThirdPassengerWeaponType", getWeaponIndex(2));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_AP, compound.getInt("LoadedAP"));
        this.entityData.set(LOADED_HE, compound.getInt("LoadedHE"));
        this.entityData.set(LOADED_AMMO_TYPE, compound.getInt("LoadedAmmoType"));
        setWeaponIndex(0, compound.getInt("WeaponType"));
        setWeaponIndex(1, compound.getInt("PassengerWeaponType"));
        setWeaponIndex(2, compound.getInt("ThirdPassengerWeaponType"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.3f) * damage);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.15), random.nextFloat() * 0.15f + 1.05f);
    }


    @Override
    public void baseTick() {
        super.baseTick();
        this.updateOBB();

//        if (getLeftTrack() < 0) {
//            setLeftTrack(100);
//        }
//
//        if (getLeftTrack() > 100) {
//            setLeftTrack(0);
//        }
//
//        if (getRightTrack() < 0) {
//            setRightTrack(100);
//        }
//
//        if (getRightTrack() > 100) {
//            setRightTrack(0);
//        }

        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
            this.entityData.set(GUN_FIRE_TIME, this.entityData.get(GUN_FIRE_TIME) - 1);
        }

        if (reloadCoolDown == 120 && this.getFirstPassenger() instanceof Player player) {
            SoundTool.playLocalSound(player, DRModSounds.ZTZ99A_RELOAD.get());
        }

//        if (reloadCoolDown == 100 && this.getFirstPassenger() instanceof Player player) {
//            SoundTool.playLocalSound(player, DRModSounds.ZTZ99A_CARTRIDGE.get());
//        }

        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;
            for (int i = 0; i < getMaxPassengers(); i++) {
                if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }

            if (reloadCoolDown > 0 && (
                    (entityData.get(LOADED_AMMO_TYPE) == 0 && (hasCreativeAmmo || countItem(ModItems.AP_5_INCHES.get()) > 0)) ||
                            (entityData.get(LOADED_AMMO_TYPE) == 1 && (hasCreativeAmmo || countItem(ModItems.HE_5_INCHES.get()) > 0))
            )) {
                reloadCoolDown--;
            }

            if (this.entityData.get(RELOAD_COOLDOWN) > 0) {
                this.entityData.set(RELOAD_COOLDOWN, this.entityData.get(RELOAD_COOLDOWN) - 1);
            }

            this.handleAmmo();
        }

        if (this.onGround()) {
            float f0 = 0.6f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.99, f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        if (this.isInWater()) {
            float f1 = (float) (0.7f - (0.04f * Math.min(getSubmergedHeight(this), this.getBbHeight())) + 0.08f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.04 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.85, f1));
        }

        if (this.level() instanceof ServerLevel serverLevel && this.isInWater() && this.getDeltaMovement().length() > 0.1) {
            sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 4 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
            sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 10 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
        }

        turretAngle(5, 5);
        gunnerAngle(15, 15);
        lowHealthWarning();

        terrainCompact(4.0f, 5.0f);

        inertiaRotate(1.2f);

        releaseSmokeDecoy(getTurretVector(1));

        this.refreshDimensions();

    }

    private void handleAmmo() {
        if (getWeaponIndex(0) == 0 || getWeaponIndex(0) == 1) {
            entityData.set(LOADED_AMMO_TYPE, getWeaponIndex(0));
        }

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers(); i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (hasCreativeAmmo) {
            this.entityData.set(AMMO, 9999);
            this.entityData.set(MG_AMMO, 9999);
        } else {
            this.entityData.set(AMMO, countItem(getWeapon(0).ammo));
            this.entityData.set(MG_AMMO, countItem(getWeapon(1).ammo));
        }

        if ((this.getEntityData().get(LOADED_AP) == 0 || this.getEntityData().get(LOADED_HE) == 0)
                && reloadCoolDown <= 0
                && (hasCreativeAmmo || hasItem(getWeapon(0).ammo))
        ) {

            if (entityData.get(LOADED_AMMO_TYPE) == 0 && entityData.get(LOADED_AP) == 0) {
                this.entityData.set(LOADED_AP, 1);
                if (!hasCreativeAmmo) {
                    consumeItem(ModItems.AP_5_INCHES.get(), 1);
                }
            }

            if (entityData.get(LOADED_AMMO_TYPE) == 1 && entityData.get(LOADED_HE) == 0) {
                this.entityData.set(LOADED_HE, 1);
                if (!hasCreativeAmmo) {
                    consumeItem(ModItems.HE_5_INCHES.get(), 1);
                }
            }
        }
    }

    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.05 || Mth.abs(this.entityData.get(POWER)) > 0.1;
    }

    @Override
    public boolean canCollideBlockBeastly() {
        return getDeltaMovement().horizontalDistance() > 0.3;
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (type == 0) {
            if (reloadCoolDown == 0 && (getWeaponIndex(0) == 0 || getWeaponIndex(0) == 1)) {
//                if (!this.canConsume(VehicleConfig.YX_100_SHOOT_COST.get())) {
//                    player.displayClientMessage(Component.translatable("tips.superbwarfare.annihilator.energy_not_enough").withStyle(ChatFormatting.RED), true);
//                    return;
//                }

                Matrix4f transform = getBarrelTransform(1);
                Vector4f worldPosition = transformPosition(transform, 0, 0, 0);

                var cannonShell = (CannonShellWeapon) getWeapon(0);
                var entityToSpawn = cannonShell.create(player);

                entityToSpawn.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                entityToSpawn.shoot(getBarrelVector(1).x, getBarrelVector(1).y + 0.005f, getBarrelVector(1).z, cannonShell.velocity, 0.02f);
                level().addFreshEntity(entityToSpawn);

                if (!player.level().isClientSide) {
                    playShootSound3p(player, 0, 8, 16, 32);
                }

                this.entityData.set(CANNON_RECOIL_TIME, 40);

                if (getWeaponIndex(0) == 0) {
                    this.entityData.set(LOADED_AP, 0);
                } else if (getWeaponIndex(0) == 1) {
                    this.entityData.set(LOADED_HE, 0);
                }

                //this.consumeEnergy(10000);
                this.entityData.set(YAW, getTurretYRot());

                reloadCoolDown = DragonRiseServerConfig.ZTZ99A_CANNON_COOLDOWN.get();

                this.getEntityData().set(RELOAD_COOLDOWN,reloadCoolDown);

                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX() + 5 * getBarrelVector(1).x,
                            this.getY() + 0.1,
                            this.getZ() + 5 * getBarrelVector(1).z,
                            300, 6, 0.02, 6, 0.005);

                    double x = worldPosition.x + 9 * getBarrelVector(1).x;
                    double y = worldPosition.y + 9 * getBarrelVector(1).y;
                    double z = worldPosition.z + 9 * getBarrelVector(1).z;

                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
                    server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);

                    int count = 6;

                    for (float i = 9.5f; i < 23; i += .5f) {
                        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                worldPosition.x + i * getBarrelVector(1).x,
                                worldPosition.y + i * getBarrelVector(1).y,
                                worldPosition.z + i * getBarrelVector(1).z,
                                Mth.clamp(count--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
                    }

                    Vector4f worldPositionL = transformPosition(transform, -0.35f, 0, 0);
                    Vector4f worldPositionR = transformPosition(transform, 0.35f, 0, 0);

                    for (float i = 3f; i < 6; i += .5f) {
                        server.sendParticles(ParticleTypes.CLOUD,
                                worldPositionL.x + i * getBarrelVector(1).x,
                                worldPositionL.y + i * getBarrelVector(1).y,
                                worldPositionL.z + i * getBarrelVector(1).z,
                                1, 0.025, 0.025, 0.025, 0.0015);

                        server.sendParticles(ParticleTypes.CLOUD,
                                worldPositionR.x + i * getBarrelVector(1).x,
                                worldPositionR.y + i * getBarrelVector(1).y,
                                worldPositionR.z + i * getBarrelVector(1).z,
                                1, 0.025, 0.025, 0.025, 0.0015);
                    }
                }

                ShakeClientMessage.sendToNearbyPlayers(this, 8, 10, 8, 60);
            } else if (getWeaponIndex(0) == 2) {
                if (this.cannotFireCoax) return;

                Matrix4f transform = getBarrelTransform(1);
                Vector4f worldPosition = transformPosition(transform, -0.4f, 0.15f, 2f);

                if (this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo) {
                    var projectileRight = ((ProjectileWeapon) getWeapon(0)).create(player).setGunItemId(this.getType().getDescriptionId() + ".1");

                    projectileRight.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                    projectileRight.shoot(player, getBarrelVector(1).x, getBarrelVector(1).y + 0.005f, getBarrelVector(1).z, 36,
                            0.25f);
                    this.level().addFreshEntity(projectileRight);

                    if (!hasCreativeAmmo) {
                        ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                            if (stack.is(ModItems.AMMO_BOX.get())) {
                                return Ammo.HEAVY.get(stack) > 0;
                            }
                            return false;
                        }).findFirst().orElse(ItemStack.EMPTY);

                        if (!ammoBox.isEmpty()) {
                            Ammo.HEAVY.add(ammoBox, -1);
                        } else {
                            this.getItemStacks().stream().filter(stack -> stack.is(ModItems.HEAVY_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                        }
                    }
                }

                this.entityData.set(COAX_HEAT, this.entityData.get(COAX_HEAT) + 4);
                this.entityData.set(FIRE_ANIM, 2);

                if (!player.level().isClientSide) {
                    playShootSound3p(player, 0, 4, 12, 24);
                }
            }
        }

        if (type == 1) {
            if (this.cannotFire) return;
            Matrix4f transform = getGunTransform(1);
            Vector4f worldPosition = transformPosition(transform, 0, -0.25f, 0);

            var projectile = (ProjectileWeapon) getWeapon(1);
            var projectileEntity = projectile.create(player).setGunItemId(this.getType().getDescriptionId() + ".2");

            projectileEntity.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
            projectileEntity.shoot(getGunnerVector(1).x, getGunnerVector(1).y + 0.01f, getGunnerVector(1).z, 20, 0.3f);

            this.level().addFreshEntity(projectileEntity);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 1, 4, 12, 24);
            }

            this.entityData.set(GUN_FIRE_TIME, 2);
            this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);

            ShakeClientMessage.sendToNearbyPlayers(this, 4, 6, 4, 6);

            if (hasCreativeAmmo) return;

            ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                if (stack.is(ModItems.AMMO_BOX.get())) {
                    return Ammo.HEAVY.get(stack) > 0;
                }
                return false;
            }).findFirst().orElse(ItemStack.EMPTY);

            if (!ammoBox.isEmpty()) {
                Ammo.HEAVY.add(ammoBox, -1);
            } else {
                consumeItem(getWeapon(1).ammo, 1);
            }
        }

    }


    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();

        if (this.getEnergy() <= 0) return;

        if (!(passenger0 instanceof Player)) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
            this.entityData.set(POWER, 0f);
        }

        if (forwardInputDown) {
            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + (this.entityData.get(POWER) < 0 ? 0.002f : 0.0012f) * (1 + getXRot() / 55), 0.21f));
        }

        if (backInputDown) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0 ? 0.002f : 0.0012f) * (1 - getXRot() / 55), -0.025f));
            if (rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.15f);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.15f);
            }
        } else {
            if (rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.15f);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.15f);
            }
        }

        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(DragonRiseServerConfig.ZTZ99A_ENERGY_COST.get());
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * (upInputDown ? 0.5f : (rightInputDown || leftInputDown) ? 0.967f : 0.98f));
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * (float) Math.max(0.76f - 0.1f * this.getDeltaMovement().horizontalDistance(), 0.3));

        double s0 = getDeltaMovement().dot(this.getViewVector(1));

        this.setLeftWheelRot((float) ((this.getLeftWheelRot() - 1.25 * s0) + Mth.clamp(0.75f * this.entityData.get(DELTA_ROT), -5f, 5f)));
        this.setRightWheelRot((float) ((this.getRightWheelRot() - 1.25 * s0) - Mth.clamp(0.75f * this.entityData.get(DELTA_ROT), -5f, 5f)));

        setLeftTrack((float) ((getLeftTrack() - 1.5 * Math.PI * s0) + Mth.clamp(0.4f * Math.PI * this.entityData.get(DELTA_ROT), -5f, 5f)));
        setRightTrack((float) ((getRightTrack() - 1.5 * Math.PI * s0) - Mth.clamp(0.4f * Math.PI * this.entityData.get(DELTA_ROT), -5f, 5f)));

        int i;

        if (entityData.get(L_WHEEL_DAMAGED) && entityData.get(R_WHEEL_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.93f);
            i = 0;
        } else if (entityData.get(L_WHEEL_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.975f);
            i = 3;
        } else if (entityData.get(R_WHEEL_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.975f);
            i = -3;
        } else {
            i = 0;
        }

        if (entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.85f);
        }

        this.setYRot((float) (this.getYRot() - (isInWater() && !onGround() ? 2.5 : 6) * entityData.get(DELTA_ROT) - i * s0));
        if (this.isInWater() || onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale((!isInWater() && !onGround() ? 0.13f : (isInWater() && !onGround() ? 2 : 2.4f)) * this.entityData.get(POWER))));
        }
    }

    @Override
    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        super.move(movementType, movement);
        if (this.isInWater() && horizontalCollision) {
            setDeltaMovement(this.getDeltaMovement().add(0, 0.07, 0));
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.YX_100_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Math.max(Mth.abs(entityData.get(POWER)), Mth.abs(0.1f * this.entityData.get(DELTA_ROT))) * 2.5f;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getTurretTransform(1);
        Matrix4f transformVehicle = getVehicleTransform(1);

        int i = this.getOrderedPassengers().indexOf(passenger);

        var worldPosition = switch (i) {
            case 0 -> transformPosition(transform, 0.8f, -0.7f, 0.3f);
            case 1 -> transformPosition(transform, -0.5625f, 0.1f, -0.57275625f);
            case 2 -> transformPosition(transformVehicle, 0.475f, 0.5f, 2.1f);
            case 3 -> transformPosition(transformVehicle, 1.625f, 0.5f, -5.125f);
            case 4 -> transformPosition(transformVehicle, -1.625f, 0.5f, -5.125f);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };

        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYBodyRot(getBarrelYRot(1));
        }
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getTurretTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1f, 0.6076875f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public int getMaxPassengers() {
        return 5;
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    @Override
    public Vec3 getGunnerVector(float pPartialTicks) {
        Matrix4f transform = getGunnerBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0f, 0.05f, 0.6477125f);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = getTurretYaw(ticks);

        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float x = Mth.lerp(ticks, turretXRotO, getTurretXRot());
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    @Override
    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0, 2.1059375f, 0.1875f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }

    public Matrix4f getGunTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, -0.75f, 1.1F, -0.5f);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, gunYRotO, getGunYRot()) - Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformT;
    }

    public Matrix4f getGunnerBarrelTransform(float ticks) {
        Matrix4f transformG = getGunTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0f, 1.35984375f, 0.0551625f);

        transformG.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = getTurretYaw(ticks);

        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float x = Mth.lerp(ticks, gunXRotO, getGunXRot());
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformG.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformG;
    }

    @Override
    public float rotateYOffset() {
        return 0.5f;
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 200f,
                    this.getX(), this.getY(), this.getZ(), 16f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        super.destroy();
    }

    protected void clampRotation(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (entity.level().isClientSide && entity == getFirstPassenger()) {
            float a = getTurretYaw(1);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = -(180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            float min = -15f - r * getXRot() - r2 * getRoll();
            float max = 7f - r * getXRot() - r2 * getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getBarrelYRot(1));
                float f3 = Mth.clamp(f2, -20.0F, 20.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(getBarrelYRot(1));
            }
        } else if (entity == getNthEntity(1)) {
            float a = getTurretYaw(1);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = -(180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            float min = -60f - r * getXRot() - r2 * getRoll();
            float max = 15f - r * getXRot() - r2 * getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getGunYRot(1));
                float f3 = Mth.clamp(f2, -150.0F, 150.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(entity.getYRot());
            }
        }
//        } else if (entity == getNthEntity(2)) {
//            float a = getTurretYaw(1);
//            float r = (Mth.abs(a) - 90f) / 90f;
//
//            float r2;
//
//            if (Mth.abs(a) <= 90f) {
//                r2 = a / 90f;
//            } else {
//                if (a < 0) {
//                    r2 = -(180f + a) / 90f;
//                } else {
//                    r2 = (180f - a) / 90f;
//                }
//            }
//
//            float min = -90f - r * getXRot() - r2 * getRoll();
//            float max = 22.5f - r * getXRot() - r2 * getRoll();
//
//            float f = Mth.wrapDegrees(entity.getXRot());
//            float f1 = Mth.clamp(f, min, max);
//            entity.xRotO += f1 - f;
//            entity.setXRot(entity.getXRot() + f1 - f);
//        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState cannonShootPredicate(AnimationState<ZTZ99AEntity> event) {
        if (this.entityData.get(CANNON_RECOIL_TIME) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.ztz99a.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.ztz99a.idle"));
    }
//
//    private PlayState coaxShootPredicate(AnimationState<ZTZ99AEntity> event) {
//        if (this.entityData.get(FIRE_ANIM) > 0) {
//            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.yx100.fire_coax"));
//        }
//        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.yx100.idle_coax"));
//    }
//
//    private PlayState gunShootPredicate(AnimationState<ZTZ99AEntity> event) {
//        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
//            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.yx100.fire2"));
//        }
//        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.yx100.idle2"));
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
//        data.add(new AnimationController<>(this, "coax", 0, this::coaxShootPredicate));
//        data.add(new AnimationController<>(this, "gun", 0, this::gunShootPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int mainGunRpm(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0 || getWeaponIndex(0) == 1) {
                return 10;
            } else if (getWeaponIndex(0) == 2) {
                return 500;
            }
        }

        if (player == getNthEntity(1)) {
            return 500;
        }

//        if (player == getNthEntity(2)) {
//            return 600;
//        }

        return 10;
    }

    @Override
    public boolean canShoot(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return this.entityData.get(LOADED_AP) > 0;
            } else if (getWeaponIndex(0) == 1) {
                return this.entityData.get(LOADED_HE) > 0;
            } else if (getWeaponIndex(0) == 2) {
                return (this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFireCoax;
            }
        }

        if (player == getNthEntity(1)) {
            return (this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return this.entityData.get(LOADED_AP);
            } else if (getWeaponIndex(0) == 1) {
                return this.entityData.get(LOADED_HE);
            } else if (getWeaponIndex(0) == 2) {
                return this.entityData.get(MG_AMMO);
            }
        }

        if (player == getNthEntity(1)) {
            return this.entityData.get(MG_AMMO);
        }
        return 0;
    }

    @Override
    public boolean banHand(Player player) {
        if (player == getNthEntity(0) || player == getNthEntity(1)) {
            return true;
        }
        return player == getNthEntity(2) && !player.isShiftKeyDown();
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return entity == getNthEntity(0) || entity == getNthEntity(1) || entity == getNthEntity(2);
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public boolean hasTracks() {
        return true;
    }

    @Override
    public int getWeaponHeat(Player player) {
        if (player == getNthEntity(0)) {
            return entityData.get(COAX_HEAT);
        }

        if (player == getNthEntity(1)) {
            return entityData.get(HEAT);
        }

        return 0;
    }

    @Override
    public void changeWeapon(int index, int value, boolean isScroll) {
        if (index != 0) return;

        var weapons = getAvailableWeapons(index);
        if (weapons.isEmpty()) return;
        var count = weapons.size();

        var typeIndex = isScroll ? (value + getWeaponIndex(index) + count) % count : value;

        if (typeIndex == 0 || typeIndex == 1) {
            if (entityData.get(LOADED_AP) > 0 && typeIndex == 1) {
                if (this.getFirstPassenger() instanceof Player player && !InventoryTool.hasCreativeAmmoBox(player)) {
                    this.insertItem(ModItems.AP_5_INCHES.get(), 1);
                }
                entityData.set(LOADED_AP, 0);
            }

            if (entityData.get(LOADED_HE) > 0 && typeIndex == 0) {
                if (this.getFirstPassenger() instanceof Player player && !InventoryTool.hasCreativeAmmoBox(player)) {
                    this.insertItem(ModItems.HE_5_INCHES.get(), 1);
                }
                entityData.set(LOADED_HE, 0);
            }

            if (typeIndex != entityData.get(LOADED_AMMO_TYPE)) {
                this.reloadCoolDown = DragonRiseServerConfig.ZTZ99A_CANNON_COOLDOWN.get();

                this.getEntityData().set(RELOAD_COOLDOWN,reloadCoolDown);
            }

            if (this.getFirstPassenger() instanceof ServerPlayer player) {
                var clientboundstopsoundpacket = new ClientboundStopSoundPacket(DRModSounds.ZTZ99A_RELOAD.get().getLocation(), SoundSource.PLAYERS);
                player.connection.send(clientboundstopsoundpacket);
            }
        }

        WeaponVehicleEntity.super.changeWeapon(index, value, isScroll);
    }

    public Vec3 getGunVec(float ticks) {
        return getGunnerVector(ticks);
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return new ResourceLocation(com.modernwarfare.dragonrise.Mod.MODID, "textures/vehicle_icon/ztz99a_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        float minWH = (float) Math.min(screenWidth, screenHeight);
        float scaledMinWH = Mth.floor(minWH * scale);
        float centerW = ((screenWidth - scaledMinWH) / 2);
        float centerH = ((screenHeight - scaledMinWH) / 2);
        System.out.println(1);
        float coolDown = this.getEntityData().get(RELOAD_COOLDOWN) / 20.0F;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // 准心
        if (this.getWeaponIndex(0) == 0) {
            //blit(guiGraphics.pose(),Mod.loc("textures/screens/land/tank_cannon_cross_ap.png"),centerW,centerH,0,0.0F,scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, Mth.hsvToRgb( (1-coolDown / 6F)/ 6F, 1.0F, 1.0F));
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/tank_cannon_cross_ap.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 1) {
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/tank_cannon_cross_he.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 2) {
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 3) {
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_missile_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        }


        // 武器名称
        if (this.getWeaponIndex(0) == 0) {
            guiGraphics.drawString(font, Component.literal("AP SHELL  " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, 0x66FF00, false);
        } else if (this.getWeaponIndex(0) == 1) {
            guiGraphics.drawString(font, Component.literal("HE SHELL  " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, 0x66FF00, false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat = 1 - this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal(" 12.7MM HMG " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        }


        guiGraphics.drawString(font, Component.literal("CD  " + String.format("%.1f",coolDown) ), screenWidth / 2 + 15, screenHeight / 2 + 15, Mth.hsvToRgb( (1-coolDown / 6F)/ 4F, 1.0F, 1.0F), false);
//        if (player == getNthEntity(1)) {
//            System.out.println(2);
//            if (this.getWeaponIndex(1) == 0) {
//                preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
//            }
//            if (this.getWeaponIndex(1) == 0) {
//                double heat = 1 - this.getEntityData().get(HEAT) / 100.0F;
//                guiGraphics.drawString(font, Component.literal(" 12.7MM NSVT " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
//            }
//        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            guiGraphics.drawString(font, Component.literal("AP SHELL " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 1) {
            guiGraphics.drawString(font, Component.literal("HE SHELL " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("12.7MM HMG " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public void releaseSmokeDecoy(Vec3 vec3) {
        if (decoyInputDown) {
            if (this.entityData.get(DECOY_COUNT) > 0 && this.level() instanceof ServerLevel) {
                Entity passenger = getFirstPassenger();
                for (int i = 0; i < 8; i++) {
                    SmokeDecoyEntity smokeDecoyEntity = new SmokeDecoyEntity((LivingEntity) passenger, this.level());
                    smokeDecoyEntity.setPos(this.getX(), this.getY() + getBbHeight(), this.getZ());
                    smokeDecoyEntity.decoyShoot(this, vec3.yRot((-78.75f + 22.5F * i) * Mth.DEG_TO_RAD), 4f, 8);
                    this.level().addFreshEntity(smokeDecoyEntity);
                }
                this.level().playSound(null, this, ModSounds.DECOY_FIRE.get(), this.getSoundSource(), 1, 1);
                decoyReloadCoolDown = 500;
                this.getEntityData().set(DECOY_COUNT, this.getEntityData().get(DECOY_COUNT) - 1);
            }
            decoyInputDown = false;
        }
        if (this.entityData.get(DECOY_COUNT) < 5 && decoyReloadCoolDown == 0 && this.level() instanceof ServerLevel) {
            this.entityData.set(DECOY_COUNT, this.entityData.get(DECOY_COUNT) + 1);
            this.level().playSound(null, this, ModSounds.DECOY_RELOAD.get(), this.getSoundSource(), 1, 1);
            decoyReloadCoolDown = 500;
        }
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        if (seatIndex == 0) {
            return zoom ? 0.17 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.22 : 0.35;
        } else if (seatIndex == 1) {
            return zoom ? 0.25 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.35 : 0.4;
        } else return original;
    }

    @Override
    public boolean isEnclosed(int index) {
        return index != 2;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                return new Vec2((float) -getYRotFromVector(this.getBarrelVec(partialTicks)), (float) -getXRotFromVector(this.getBarrelVec(partialTicks)));
            } else if (this.getSeatIndex(player) == 1) {
                return new Vec2((float) -getYRotFromVector(this.getGunnerVector(partialTicks)), (float) -getXRotFromVector(this.getGunnerVector(partialTicks)));
            }
        }
        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                if (zoom) {
                    return new Vec3(this.driverZoomPos(partialTicks).x, this.driverZoomPos(partialTicks).y, this.driverZoomPos(partialTicks).z);
                } else {
                    return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
                }
            } else if (this.getSeatIndex(player) == 1) {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    @Override
    public float getTurretMaxHealth() {
        return 150;
    }

    @Override
    public float getWheelMaxHealth() {
        return 100;
    }

    @Override
    public float getEngineMaxHealth() {
        return 100;
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obbBody1, this.obbBody2, this.obbWheelLeft, this.obbWheelRight, this.obbEngine, this.obbTurret, this.obbTurret2);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0, 1.1625f, -0.2625f);
        this.obbBody1.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obbBody1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0, 1.8f, -2.0225f);
        this.obbBody2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obbBody2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.675f, 0.84375f, -0.025f);
        this.obbWheelLeft.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obbWheelLeft.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.675f, 0.84375f, -0.025f);
        this.obbWheelRight.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obbWheelRight.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, 0, 1.55f, -3.525f);
        this.obbEngine.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obbEngine.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);

        Vector4f worldPositionT = transformPosition(transformT, 0, 0.335f, -1.125f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));

        Vector4f worldPositionT2 = transformPosition(transformT, 0, 0.175f, 0.95625f);
        this.obbTurret2.center().set(new Vector3f(worldPositionT2.x, worldPositionT2.y, worldPositionT2.z));
        this.obbTurret2.setRotation(VectorTool.combineRotationsTurret(1, this));
    }
}
