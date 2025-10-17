package com.modernwarfare.dragonrise.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import com.atsuishio.superbwarfare.entity.projectile.WgMissileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.WgMissileWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags.EntityTypes;
import com.atsuishio.superbwarfare.tools.*;
import com.atsuishio.superbwarfare.tools.OBB.Part;
import com.modernwarfare.dragonrise.config.Z10OBBconfig;
import com.modernwarfare.dragonrise.config.server.DragonRiseServerConfig;
import com.modernwarfare.dragonrise.init.ModEntities;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class ZHI10MEEntity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity, OBBEntity {
    private final AnimatableInstanceCache cache;
    public static final EntityDataAccessor<Float> PROPELLER_ROT;
    public static final EntityDataAccessor<Integer> LOADED_ROCKET;
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUNFINETIME1 = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public boolean engineStart;
    public boolean engineStartOver;
    public int fireIndex;
    public int fireWGIndex;
    public int holdTick;
    public int holdPowerTick;
    public float destroyRot;
    public float delta_x;
    public float delta_y;
    public int recooldownWG;
    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;
    public OBB obb7;
    public OBB obb8;
    public OBB obb9;

    private static final Z10OBBconfig OBB_CONFIG = Z10OBBconfig.HANDLER.getConfig();

    public ZHI10MEEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ZHI10ME.get(), world);
    }

    public ZHI10MEEntity(EntityType<ZHI10MEEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb1())), new Quaternionf(), Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb2())), new Quaternionf(), Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb3())), new Quaternionf(), Part.BODY);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb4())), new Quaternionf(), Part.BODY);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb5())), new Quaternionf(), Part.ENGINE1);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb6())), new Quaternionf(), Part.ENGINE2);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb7())), new Quaternionf(), Part.BODY);
        this.obb8 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb8())), new Quaternionf(), Part.BODY);
        this.obb9 = new OBB(this.position().toVector3f(), new Vector3f(getV3FromOBB(OBB_CONFIG.getObb9())), new Quaternionf(), Part.BODY);
    }

    private Vector3f getV3FromOBB(Map<String, Map<String,Float>> OBB){
        return new Vector3f(
                OBB.get("Vector3f").get("x")/32,
                OBB.get("Vector3f").get("y")/32,
                OBB.get("Vector3f").get("z")/32
        );
    }
    private Vector3f getTFFromOBB(Map<String, Map<String,Float>> OBB){
        return new Vector3f(
                OBB.get("transform").get("x")/16+getV3FromOBB(OBB).x,
                OBB.get("transform").get("y")/16+getV3FromOBB(OBB).y,
                OBB.get("transform").get("z")/16+getV3FromOBB(OBB).z
        );
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                //主武器
                new VehicleWeapon[]{
                        //火箭弹
                        new SmallRocketWeapon()
                                .damage(DragonRiseServerConfig.ZHI10ME_ROCKET_DAMAGE.get().floatValue())
                                .explosionDamage(DragonRiseServerConfig.ZHI10ME_ROCKET_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(DragonRiseServerConfig.ZHI10ME_ROCKET_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.SMALL_ROCKET_FIRE_1P.get())
                                .sound3p(ModSounds.SMALL_ROCKET_FIRE_3P.get())
                },
                new VehicleWeapon[]{
                        // 吊舱机炮
                        new SmallCannonShellWeapon()
                                .blockInteraction(DragonRiseServerConfig.ZHI10ME_CANNON_DESTROY.get() ? BlockInteraction.DESTROY : BlockInteraction.KEEP)
                                .damage(DragonRiseServerConfig.ZHI10ME_CANNON_DAMAGE.get().floatValue())
                                .explosionDamage(DragonRiseServerConfig.ZHI10ME_CANNON_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(DragonRiseServerConfig.ZHI10ME_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png"))
                                .sound1p(ModSounds.HELICOPTER_CANNON_FIRE_1P.get())
                                .sound3p(ModSounds.HELICOPTER_CANNON_FIRE_3P.get())
                                .sound3pFar(ModSounds.HELICOPTER_CANNON_FAR.get())
                                .sound3pVeryFar(ModSounds.HELICOPTER_CANNON_VERYFAR.get()),
                        new WgMissileWeapon()
                                .damage(ExplosionConfig.WIRE_GUIDE_MISSILE_DAMAGE.get())
                                .explosionDamage(ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS.get())
                                .icon(com.modernwarfare.dragonrise.Mod.loc("textures/hud/hud_akd9.png"))
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.BMP_MISSILE_FIRE_1P.get())
                                .sound3p(ModSounds.BMP_MISSILE_FIRE_3P.get()),
                }
        };
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(7.0F, 1.0F, -2.7);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(PROPELLER_ROT, 0.0F);
        this.entityData.define(GUNFINETIME1, 0);
        this.entityData.define(MISSILE_COUNT, 0);

        this.entityData.define(LOADED_MISSILE, 0);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MG_AMMO", this.entityData.get(MG_AMMO));
        compound.putInt("LoadedRocket", this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", this.entityData.get(PROPELLER_ROT));

        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(MG_AMMO, compound.getInt("MG_AMMO"));
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));

        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> {
                    Entity entity = source.getDirectEntity();
                    if (entity != null && entity.getType().is(EntityTypes.AERIAL_BOMB)) {
                        damage = damage * 2.0F;
                    }

                    damage = damage * (this.getHealth() > 0.1F ? 0.7F : 0.05F);
                    return damage;
                });
    }

    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && this.entityData.get(LOADED_ROCKET) < 14) {
            this.entityData.set(LOADED_ROCKET, this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else {
            return super.interact(player, hand);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.updateOBB();

        if (this.entityData.get(GUNFINETIME1) > 0) {
            this.entityData.set(GUNFINETIME1, this.entityData.get(GUNFINETIME1) - 1);
        }
        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDown > 0) {
                --this.reloadCoolDown;
            }
            if(this.recooldownWG>0){
                --this.recooldownWG;
            }

            this.handleAmmo();
        }

        this.releaseDecoy();
        this.lowHealthWarning();
        this.terrainCompact(2.7F, 2.7F);

        this.refreshDimensions();
        gunnerAngle();

    }

    @Override
    public float getGunYRot() {
        if(gunYRot>80){
            gunYRot=80;
            return gunYRot;
        }else if(gunYRot<-80){
            gunYRot=-80;
            return gunYRot;
        }
        return gunYRot;
    }

    private void handleAmmo() {

        if (!(this.getFirstPassenger() instanceof Player)) {
            if(!(this.getNthEntity(1) instanceof Player)){
                return;
            }
        }

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers(); i++) {if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {hasCreativeAmmo = true;}}

        if ((hasItem(ModItems.SMALL_ROCKET.get()) || hasCreativeAmmo)
                && reloadCoolDown == 0
                && this.getEntityData().get(LOADED_ROCKET) < 14) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 25;
            if (!hasCreativeAmmo) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_ROCKET.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if ((hasItem(ModItems.WIRE_GUIDE_MISSILE.get()) || hasCreativeAmmo)
                && this.recooldownWG <= 0
                && this.getEntityData().get(LOADED_MISSILE) < 1) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            this.recooldownWG = 160;
            if (!hasCreativeAmmo) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.WIRE_GUIDE_MISSILE.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BMP_MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        }
        if(this.getWeaponIndex(1)==0){
            this.entityData.set(MG_AMMO, countItem(ModItems.SMALL_SHELL.get()));
            if(hasCreativeAmmo){this.entityData.set(MG_AMMO, 1);}
        }else if(this.getWeaponIndex(1)==1) {
            this.entityData.set(MG_AMMO, this.getEntityData().get(LOADED_MISSILE));
        }

        this.entityData.set(MISSILE_COUNT, countItem(ModItems.WIRE_GUIDE_MISSILE.get()));
    }

    @Override
    public void travel() {
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1, 0.8));
        } else {
            setZRot(getRoll() * (backInputDown ? 0.9f : 0.99f));
            float f = (float) Mth.clamp(0.95f - 0.015 * getDeltaMovement().length() + 0.02f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((this.getXRot() < 0 ? -0.035 : (this.getXRot() > 0 ? 0.035 : 0)) * this.getDeltaMovement().length())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.95, f));
        }

        if (this.isInWater() && this.tickCount % 4 == 0 && getSubmergedHeight(this) > 0.5 * getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), 6 + (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
        }

        Entity passenger = this.getFirstPassenger();
        Entity passenger2 = this.getNthEntity(1);
        float diffX;
        float diffZ;

        if (this.getHealth() > 0.1F * this.getMaxHealth()) {
            if (passenger == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.upInputDown = false;
                this.downInputDown = false;
                this.setZRot(this.roll * 0.98F);
                this.setXRot(this.getXRot() * 0.98F);
                if (passenger2 == null) {
                    this.entityData.set(POWER, this.entityData.get(POWER) * 0.99F);
                }
            } else if (passenger instanceof Player player) {
                this.delta_x = (this.onGround() ? 0.0F : 1.5F) * this.entityData.get(MOUSE_SPEED_Y) * this.entityData.get(PROPELLER_ROT);
                this.delta_y = Mth.clamp((this.onGround() ? 0.1F : 2.0F) * this.entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT) + (float) (this.entityData.get(ENGINE2_DAMAGED) ? 25 : 0) * this.entityData.get(PROPELLER_ROT), -10.0F, 10.0F);
                if (!entityData.get(LANDING_INPUT_DOWN) || findNearestLandingPos(30) == null) {
                    if (this.rightInputDown) {
                        this.holdTick++;
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.28F * (float) Math.min(this.holdTick, 14) * this.entityData.get(POWER));
                    } else if (this.leftInputDown) {
                        this.holdTick++;
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.28F * (float) Math.min(this.holdTick, 14) * this.entityData.get(POWER));
                    } else {
                        this.holdTick = 0;
                    }

                    this.setXRot(this.getXRot() + this.delta_x);
                    this.setZRot(this.getRoll() - this.entityData.get(DELTA_ROT) + (this.onGround() ? 0.0F : 0.25F) * this.entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT));
                }
                this.setYRot(this.getYRot() + delta_y);
                if (findNearestLandingPos(30) != null && !onGround() && entityData.get(LANDING_INPUT_DOWN)) {
                    this.updateAutoLanding(findNearestLandingPos(30));
                }

                if (level().isClientSide && findNearestLandingPos(30) != null && !onGround()) {
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.press_s_to_landing"), true);
                }
            }
            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = this.upInputDown || this.forwardInputDown;
                    boolean down = this.downInputDown;

                    if (!this.engineStart && up) {
                        this.engineStart = true;
                        this.level().playSound(null, this, ModSounds.HELICOPTER_ENGINE_START.get(), this.getSoundSource(), 3.0F, 1.0F);
                    }

                    if (up && this.engineStartOver) {
                        this.holdPowerTick++;
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 7.0E-4F * (float)Math.min(this.holdPowerTick, 10), 0.12F));
                    }

                    if (this.engineStartOver) {
                        if (down) {
                            this.holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.025F));
                        } else if (this.backInputDown) {
                            this.holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.052F));
                        }
                    }

                    if (this.engineStart && !this.engineStartOver) {
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0012F, 0.045F));
                    }

                    if (!(up || down || backInputDown) && engineStartOver) {
                        if (this.getDeltaMovement().y() < 0) {
                            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 2.0E-4F, 0.12F));
                        } else {
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.onGround() ? 5.0E-5F : 2.0E-4F), 0.0F));
                        }

                        this.holdPowerTick = 0;
                    }
                } else {
                    this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 1.0E-4F, 0.0F));
                    this.forwardInputDown = false;
                    this.backInputDown = false;
                    this.engineStart = false;
                    this.engineStartOver = false;
                }
            }
        } else if (!this.onGround() && this.engineStartOver) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 3.0E-4F, 0.01F));
            this.destroyRot += 0.08F;
            diffX = 45 - this.getXRot();
            diffZ = -20 - this.getRoll();
            this.setXRot(this.getXRot() + diffX * 0.05F * this.entityData.get(PROPELLER_ROT));
            this.setYRot(this.getYRot() + this.destroyRot);
            this.setZRot(this.getRoll() + diffZ * 0.1F * this.entityData.get(PROPELLER_ROT));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0F, (double)(-this.destroyRot) * 0.004, 0.0F));
        }

        if (this.entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.98F);
        }

        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.9F);
        this.entityData.set(PROPELLER_ROT, Mth.lerp(0.18F, this.entityData.get(PROPELLER_ROT), this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30.0F * this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, this.entityData.get(PROPELLER_ROT) * 0.9995F);

        if (this.engineStart) {
            this.consumeEnergy((int)((double) DragonRiseServerConfig.ZHI10ME_MIN_ENERGY_COST.get() + (double) this.entityData.get(POWER) * ((double)(DragonRiseServerConfig.ZHI10ME_MAX_ENERGY_COST.get() - DragonRiseServerConfig.ZHI10ME_MIN_ENERGY_COST.get()) / 0.12)));
        }

        Matrix4f transform = this.getVehicleTransform(1.0F);

        Vector4f force0 = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f force1 = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);

        Vec3 force = (new Vec3(force0.x, force0.y, force0.z)).vectorTo(new Vec3(force1.x, force1.y, force1.z));

        this.setDeltaMovement(this.getDeltaMovement().add(force.scale((double) this.entityData.get(PROPELLER_ROT))));

        if (this.entityData.get(POWER) > 0.04F) {
            this.engineStartOver = true;
        }

        if (this.entityData.get(POWER) < 4.0E-4F) {
            this.engineStart = false;
            this.engineStartOver = false;
        }

    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.HELICOPTER_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return this.entityData.get(PROPELLER_ROT) * 2.0F;
    }

    protected void clampRotation(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f,-2.5F, 80.0F);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot()+ f3 - f2);
            entity.setYBodyRot(this.getYRot());
        }

    }

    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        Matrix4f transform = this.getVehicleTransform(1.0F);

        float x = 0F;
        float y = -0.5F;
        float z = 1.0F;
        y += (float)passenger.getMyRidingOffset();

        int i = this.getOrderedPassengers().indexOf(passenger);

        if (i == 0) {
            Vector4f worldPosition = this.transformPosition(transform, -x, (float) (y+0.5), z-1);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        } else if (i == 1) {
            Vector4f worldPosition = this.transformPosition(transform, x, (float) (y-0.1), (float) (z+0.4));
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        }

        if (passenger != this.getFirstPassenger()) {
            passenger.setXRot(passenger.getXRot() + (this.getXRot() - this.xRotO));
        }

        this.copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            entity.setYHeadRot(entity.getYHeadRot() + this.delta_y);
            entity.setYRot(entity.getYRot() + this.delta_y);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float g = Mth.clamp(f, -105.0F, 105.0F);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9F * this.destroyRot);
            entity.setYBodyRot(this.getYRot());
        }

    }

    @Override
    public Vec3 getBarrelVec(float pPartialTicks){
        return getGunnerVector(pPartialTicks);
    }

    public Vec3 getGunnerEyePosition(float partialTicks) {
        // 使用炮手位的变换矩阵来获取眼睛位置
        Matrix4f transform = getGunTransform(partialTicks);
        // 眼睛位置相对于炮塔的偏移（根据实际模型调整，这里假设眼睛在炮塔中心稍微上方）
        Vector4f worldPosition = transformPosition(transform, 0F, 0.1F, 0F);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp(ticks, this.xo, this.getX()), (float)Mth.lerp(ticks, this.yo + (double)1.45F, this.getY() + (double)1.45F), (float)Mth.lerp(ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, this.yRotO, this.getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, this.xRotO, this.getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }
    //炮手炮塔矩阵
    public Matrix4f getGunTransform(float ticks) {
        Matrix4f transformT = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition;
        if (level().isClientSide()) {
            worldPosition = transformPosition(transform, 0F, -0.85F, 2.6F - (float)ClientMouseHandler.custom3pDistanceLerp);
        } else {
            // 服务端使用默认值
            worldPosition = transformPosition(transform, 0F, -0.85F, 2.6F);
        }

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate(
                Axis.YP.rotationDegrees(
                        Mth.lerp(
                                ticks
                                , gunYRotO
                                , getGunYRot()
                        )
                )
        );
        return transformT;
    }

    //炮手炮管矩阵
    public Matrix4f getGunnerBarrelTransform(float ticks) {
        Matrix4f transformG = getGunTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0f, 0f, 0.0551625f);

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
        x = x>=0 ? Math.min(x,80f):Math.max(x,-80f);
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformG.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformG;
    }
    //炮手炮管向量
    public Vec3 getGunnerVector(float pPartialTicks) {
        Matrix4f transform = getGunnerBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }
    public void destroy() {
        if (this.crash) {
            this.crashPassengers();
        } else {
            this.explodePassengers();
        }

        if (this.level() instanceof ServerLevel) {
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getAttacker()), 300.0F, this.getX(), this.getY(), this.getZ(), 8.0F, ExplosionConfig.EXPLOSION_DESTROY.get() ? BlockInteraction.DESTROY : BlockInteraction.KEEP)).setDamageMultiplier(1.0F);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
        }

        super.destroy();
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, -0.83F, 0.8F);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f worldPosition2 = this.transformPosition(transform, 0.0F, 0.01F, 1.0F);
        return (new Vec3(worldPosition.x, worldPosition.y, worldPosition.z)).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();
    }

    // 获取光电球位置
    public Vec3 getLightBallPosition(float partialTicks) {
        Matrix4f transform = getLightBallTransform(partialTicks);
        Vector4f worldPosition = transformPosition(transform, 0f, 0f, 0f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    // 获取光电球方向向量
    public Vec3 getLightBallVector(float partialTicks) {
        Matrix4f transform = getLightBallTransform(partialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z)
                .vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z))
                .normalize();
    }

    // 光电球变换矩阵
    public Matrix4f getLightBallTransform(float ticks) {
        Matrix4f transformT = getVehicleTransform(ticks);

        // 应用光电球支架的水平旋转
        transformT.rotate(Axis.YP.rotationDegrees(
                Mth.lerp(ticks, gunYRotO, getGunYRot())
        ));

        // 应用光电球本体的垂直旋转
        float gunXRot = Mth.lerp(ticks, gunXRotO, getGunXRot());
        float clampedXRot = Mth.clamp(gunXRot, -90.0F, 30.0F);
        transformT.rotate(Axis.XP.rotationDegrees(-clampedXRot));

        return transformT;
    }

    @Override
    public void vehicleShoot(LivingEntity player, int type) {
        boolean hasCreativeAmmo = false;
        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                hasCreativeAmmo = true;
            }
        }
        //主
        Matrix4f transform = this.getVehicleTransform(1.0F);
        if(type == 0) {
            if (this.getWeaponIndex(0) == 0 && this.getEntityData().get(LOADED_ROCKET) > 0) {

                SmallRocketEntity heliRocketEntity = ((SmallRocketWeapon) this.getWeapon(0)).create(player);

                Vector4f worldPosition;
                Vector4f worldPosition2;
                if (this.fireIndex == 0) {

                    worldPosition = this.transformPosition(transform, 1.5F, -0.3F, 0.5F);

                    worldPosition2 = this.transformPosition(transform, 1.49F, -0.3F, 10F);
                    this.fireIndex = 1;
                } else {

                    worldPosition = this.transformPosition(transform, -1.5F, -0.5F, 0.5F);

                    worldPosition2 = this.transformPosition(transform, -1.49F, -0.5F, 10F);
                    this.fireIndex = 0;
                }
                Vec3 shootVec = (new Vec3(worldPosition.x, worldPosition.y, worldPosition.z))
                        .vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z))
                        .normalize();

                heliRocketEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7.0F, 0.25F);
                player.level().addFreshEntity(heliRocketEntity);

                if (!player.level().isClientSide) {
                    this.playShootSound3p(player, 0, 6, 6, 6, new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));
                }

                this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) - 1);
                this.reloadCoolDown = 30;
            }
        }
        if (type == 1) {
            transform = getGunnerBarrelTransform(1);

            if (getWeaponIndex(1) == 0 && !cannotFire && (this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo)) {
                Vector4f worldPosition = transformPosition(transform, 0F, -0.15F, 0);
                Vec3 shootVec = (new Vec3(
                        worldPosition.x
                        , worldPosition.y
                        , worldPosition.z)
                )
                        .vectorTo(new Vec3(
                                        (double) worldPosition.x + 0.1 * getGunnerVector(1).x
                                        , (double) worldPosition.y + 0.1 * getGunnerVector(1).y
                                        , (double) worldPosition.z + 0.1 * getGunnerVector(1).z
                                )
                        )
                        .normalize();

                SmallCannonShellEntity entityToSpawn = ((SmallCannonShellWeapon) getWeapon(1)).create(player);

                entityToSpawn.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                entityToSpawn.shoot(shootVec.x,shootVec.y,shootVec.z, 20, 0.3f);
                player.level().addFreshEntity(entityToSpawn);
                sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, worldPosition.x, worldPosition.y, worldPosition.z, 1, 0, 0, 0, 0, false);
                this.entityData.set(GUNFINETIME1, 2);
                this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);
                playShootSound3p(player, 1, 4, 12, 24,new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));
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
                        this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                    }
                }
            }

            else if (getWeaponIndex(1) == 1 && (this.getEntityData().get(LOADED_MISSILE) > 0 || hasCreativeAmmo)) {
                // 使用新的创建方法
                AKD9Entity missile = AKD9Entity.createWithLauncher(player, this.getUUID());
                //WgMissileEntity missile = ((WgMissileWeapon) this.getWeapon(1)).create(player);

                // 其余代码保持不变...
                Vector4f worldPosition = null;
                if (this.fireWGIndex == 0) {
                    worldPosition = this.transformPosition(transform, 1.7F, -0.83F, 0.8F);
                    this.fireWGIndex = 1;
                } else {
                    worldPosition = this.transformPosition(transform, -1.7F, -0.83F, 0.8F);
                    this.fireWGIndex = 0;
                }
                Vec3 forwardVector = this.getViewVector(1.0F);

                missile.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                missile.shoot(forwardVector.x, forwardVector.y, forwardVector.z, projectileVelocity(player), 0f);
                player.level().addFreshEntity(missile);
                playShootSound3p(player, 0, 6, 0, 0, getTurretShootPos(player, 1));

                this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);
                recooldownWG = 160;
            }

        }
    }

    @Override
    public int mainGunRpm(LivingEntity player) {
        return 500;
    }

    @Override
    public boolean canShoot(LivingEntity player) {
        if (player == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return this.entityData.get(AMMO) > 0;
            }
        }
        if (player == getNthEntity(1)) {
            return this.entityData.get(MG_AMMO) > 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(LivingEntity player) {
        if (player == getNthEntity(1)) {
            return this.entityData.get(MG_AMMO);
        }
        if (player == getNthEntity(0)) {
            return this.entityData.get(AMMO);
        }
        return 0;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(LivingEntity player) {
        return this.entityData.get(HEAT);
    }

    @Override
    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    @Override
    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    @Override
    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    @Override
    public float getPower() {
        return this.entityData.get(POWER);
    }
    public int getDecoy() {
        return this.entityData.get(DECOY_COUNT);
    }

    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public int passengerSeatLocation(Entity entity) {
        return entity == getNthEntity(0) ? 2 : 0;
    }
    public ResourceLocation getVehicleIcon() {
        return com.modernwarfare.dragonrise.Mod.loc("textures/vehicle_icon/Z10_icon.png");
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return seatIndex == 0 ? 0 : original;
    }

    public double getMouseSensitivity() {
        return 0.18F;
    }

    public double getMouseSpeedX() {
        return 0.16;
    }

    public double getMouseSpeedY() {
        return 0.075F;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        if (this.getSeatIndex(entity) == 2) {
            return Pair.of(
                    Axis.XP.rotationDegrees(-this.getRoll(tickDelta))
                    , Axis.ZP.rotationDegrees(this.getViewXRot(tickDelta))
            );
        } else {
            return this.getSeatIndex(entity) == 3 ? Pair.of(Axis.XP.rotationDegrees(this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(-this.getViewXRot(tickDelta))) : Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
        }
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp(ticks, this.xo, this.getX()), (float)Mth.lerp(ticks, this.yo + (double)1.45F, this.getY() + (double)1.45F), (float)Mth.lerp(ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees((float)((double)(-Mth.lerp(ticks, this.yRotO, this.getYRot())) + ClientMouseHandler.freeCameraYaw)));
        transform.rotate(Axis.XP.rotationDegrees((float)((double)Mth.lerp(ticks, this.xRotO, this.getXRot()) + ClientMouseHandler.freeCameraPitch)));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    //视角旋转
    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 1) {
            Vec2 vec2 = new Vec2(
                    (float) ((float) -getYRotFromVector(this.getGunnerVector(partialTicks)) - ClientMouseHandler.freeCameraYaw)
                    , (float) ((float) -getXRotFromVector(this.getGunnerVector(partialTicks)) - ClientMouseHandler.freeCameraPitch)
            );
            return vec2;
        }
        return this.getSeatIndex(player) == 0 ?
                new Vec2(
                        (float)((double)this.getRotY(partialTicks) - ClientMouseHandler.freeCameraYaw)
                        , (float)((double)this.getRotX(partialTicks) + ClientMouseHandler.freeCameraPitch)
                ) : super.getCameraRotation(partialTicks, player, false, false);
    }

    //摄像机位置
    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        Matrix4f transform = this.getClientVehicleTransform(partialTicks);
        //主驾驶
        if (this.getSeatIndex(player) == 0) {
            Vector4f maxCameraPosition = this.transformPosition(transform, -2.1F, 1F, -10.0F - (float)ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);
            double customEyeHeight = player.getEyeHeight() + 0.17;
            return isFirstPerson ? new Vec3(
                    Mth.lerp(partialTicks, player.xo, player.getX()),
                    Mth.lerp(partialTicks, player.yo, player.getY()) + customEyeHeight,
                    Mth.lerp(partialTicks, player.zo, player.getZ())
            ) : finalPos;
        }
        //炮手位
        else if (this.getSeatIndex(player) == 1) {
            transform.rotate(Axis.YP.rotationDegrees(getRoll()));
            Vector4f maxCameraPosition = this.transformPosition(transform, 0F, -0.953125F, 3.5734375F- (float)ClientMouseHandler.custom3pDistanceLerp);
            if(isFirstPerson) {
                if (zoom){
                    return CameraTool.getMaxZoom(transform, maxCameraPosition);
                }
                return new Vec3(
                        Mth.lerp(partialTicks, player.xo, player.getX())
                        , Mth.lerp(partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY())
                        , Mth.lerp(partialTicks, player.zo, player.getZ())
                );
            }
            return CameraTool.getMaxZoom(transform, maxCameraPosition);
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return com.modernwarfare.dragonrise.Mod.loc("textures/vehicle_icon.png");
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7,this.obb8,this.obb9);
    }

    private Vector4f transformPosition(Matrix4f transform,Vector3f vector3f) {
        return this.transformPosition(transform, vector3f.x(), vector3f.y(), vector3f.z());
    }

    public void updateOBB() {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        transform.rotate(Axis.YP.rotationDegrees(180F));
        transform.translate(0,-1.5F,-2F);
        Vector4f worldPosition = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb1()));
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition2 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb2()));
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition3 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb3()));
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition4 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb4()));
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition5 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb5()));
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition6 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb6()));
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition7 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb7()));
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition8 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb8()));
        this.obb8.center().set(new Vector3f(worldPosition8.x, worldPosition8.y, worldPosition8.z));
        this.obb8.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition9 = this.transformPosition(transform, getTFFromOBB(OBB_CONFIG.getObb9()));
        this.obb9.center().set(new Vector3f(worldPosition9.x, worldPosition9.y, worldPosition9.z));
        this.obb9.setRotation(VectorTool.combineRotations(1.0F, this));
    }

    static {
        PROPELLER_ROT = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.FLOAT);
        LOADED_ROCKET = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    }
}