package com.modernwarfare.dragonrise.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags.EntityTypes;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.atsuishio.superbwarfare.tools.OBB.Part;
import com.modernwarfare.dragonrise.config.server.DragonRiseServerConfig;
import com.modernwarfare.dragonrise.init.ModEntities;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class ZHI10MEEntity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity, OBBEntity {
    private final AnimatableInstanceCache cache;
    public static final EntityDataAccessor<Float> PROPELLER_ROT;
    public static final EntityDataAccessor<Integer> LOADED_ROCKET;
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUNFINETIME1 = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    public boolean engineStart;
    public boolean engineStartOver;
    public double velocity;
    public int fireIndex;
    public int holdTick;
    public int holdPowerTick;
    public float destroyRot;
    public float delta_x;
    public float delta_y;
    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;
    public OBB obb7;

    public ZHI10MEEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ZHI10ME.get(), world);
    }

    public ZHI10MEEntity(EntityType<ZHI10MEEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.0625F, 1.18125F, 1.625F), new Quaternionf(), Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(0.875F, 0.6875F, 0.59375F), new Quaternionf(), Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.25F, 0.3125F, 2.25F), new Quaternionf(), Part.BODY);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.0625F, 1.15625F, 0.40625F), new Quaternionf(), Part.BODY);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(1.0F, 0.25F, 0.21875F), new Quaternionf(), Part.BODY);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.3125F, 0.40625F, 0.84375F), new Quaternionf(), Part.ENGINE1);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(0.3125F, 0.40625F, 0.40625F), new Quaternionf(), Part.ENGINE2);
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                //主武器
                new VehicleWeapon[]{
                         //火箭弹
                         new SmallRocketWeapon()
                                .damage(((Double)DragonRiseServerConfig.ZHI10ME_ROCKET_DAMAGE.get()).floatValue())
                                .explosionDamage(DragonRiseServerConfig.ZHI10ME_ROCKET_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(((Double)DragonRiseServerConfig.ZHI10ME_ROCKET_EXPLOSION_RADIUS.get()).floatValue())
                                .sound((SoundEvent)ModSounds.INTO_MISSILE.get())
                                .sound1p((SoundEvent)ModSounds.SMALL_ROCKET_FIRE_1P.get())
                                .sound3p((SoundEvent)ModSounds.SMALL_ROCKET_FIRE_3P.get())
                },
                new VehicleWeapon[]{
                        // 吊舱机炮
                        new SmallCannonShellWeapon()
                                .blockInteraction((Boolean)DragonRiseServerConfig.ZHI10ME_CANNON_DESTROY.get() ? BlockInteraction.DESTROY : BlockInteraction.KEEP)
                                .damage(((Double)DragonRiseServerConfig.ZHI10ME_CANNON_DAMAGE.get()).floatValue())
                                .explosionDamage(((Double)DragonRiseServerConfig.ZHI10ME_CANNON_EXPLOSION_DAMAGE.get()).floatValue())
                                .explosionRadius(((Double)DragonRiseServerConfig.ZHI10ME_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                                .sound((SoundEvent)ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png"))
                                .sound1p((SoundEvent)ModSounds.HELICOPTER_CANNON_FIRE_1P.get())
                                .sound3p((SoundEvent)ModSounds.HELICOPTER_CANNON_FIRE_3P.get())
                                .sound3pFar((SoundEvent)ModSounds.HELICOPTER_CANNON_FAR.get())
                                .sound3pVeryFar((SoundEvent)ModSounds.HELICOPTER_CANNON_VERYFAR.get()),
                }
        };
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)7.0F, (double)1.0F, -2.7);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(PROPELLER_ROT, 0.0F);
        this.entityData.define(GUNFINETIME1, 0);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MG_AMMO", this.entityData.get(MG_AMMO));
        compound.putInt("LoadedRocket", (Integer)this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", (Float)this.entityData.get(PROPELLER_ROT));
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(MG_AMMO, compound.getInt("MG_AMMO"));
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> {
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
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && (Integer)this.entityData.get(LOADED_ROCKET) < 14) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else {
            return super.interact(player, hand);
        }
    }

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

            this.handleAmmo();
        }

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, (double)1.0F, 0.8));
        } else {
            this.setZRot(this.getRoll() * (this.backInputDown ? 0.9F : 0.99F));
            float f = (float)Mth.clamp((double)0.95F - 0.015 * this.getDeltaMovement().length() + (double)(0.02F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F), 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((this.getXRot() < 0.0F ? -0.035 : (this.getXRot() > 0.0F ? 0.035 : (double)0.0F)) * this.getDeltaMovement().length())));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.95, (double)f));
        }

        if (this.isInWater() && this.tickCount % 4 == 0 && this.getSubmergedHeight(this) > (double)0.5F * (double)this.getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(this.getFirstPassenger() == null ? this : this.getFirstPassenger())), 6.0F + (float)((double)20.0F * (this.lastTickSpeed - 0.4) * (this.lastTickSpeed - 0.4)));
        }

        gunnerAngle(15, 15);

        this.releaseDecoy();
        this.lowHealthWarning();
        this.terrainCompact(2.7F, 2.7F);
        this.refreshDimensions();
    }

    private void handleAmmo() {
        Entity firstPassenger = this.getFirstPassenger();
        if (firstPassenger == null) {
            firstPassenger = this.getNthEntity(1);
        }

        if (!(firstPassenger instanceof Player player)) {
            return;
        }

        int ammoCount = countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(ModItems.SMALL_ROCKET.get()) || InventoryTool.hasCreativeAmmoBox(player)) && reloadCoolDown == 0 && this.getEntityData().get(LOADED_ROCKET) < 14) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 25;
            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_ROCKET.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        }

        if(this.getWeaponIndex(1)==0){
            this.entityData.set(MG_AMMO, ammoCount);
        }
    }

    public void travel() {
        Entity passenger = this.getFirstPassenger();
        Entity passenger2 = this.getNthEntity(1);
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
                    this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.99F);
                }
            } else if (passenger instanceof Player) {
                if (this.rightInputDown) {
                    ++this.holdTick;
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 0.45F * (float)Math.min(this.holdTick, 18) * (Float)this.entityData.get(POWER));
                } else if (this.leftInputDown) {
                    ++this.holdTick;
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 0.45F * (float)Math.min(this.holdTick, 18) * (Float)this.entityData.get(POWER));
                } else {
                    this.holdTick = 0;
                }

                this.delta_x = (this.onGround() ? 0.0F : 1.5F) * (Float)this.entityData.get(MOUSE_SPEED_Y) * (Float)this.entityData.get(PROPELLER_ROT);
                this.delta_y = Mth.clamp((this.onGround() ? 0.1F : 2.0F) * (Float)this.entityData.get(MOUSE_SPEED_X) * (Float)this.entityData.get(PROPELLER_ROT) + (float)((Boolean)this.entityData.get(ENGINE2_DAMAGED) ? 25 : 0) * (Float)this.entityData.get(PROPELLER_ROT), -10.0F, 10.0F);
                this.setYRot(this.getYRot() + this.delta_y);
                this.setXRot(this.getXRot() + this.delta_x);
                this.setZRot(this.getRoll() - (Float)this.entityData.get(DELTA_ROT) + (this.onGround() ? 0.0F : 0.25F) * (Float)this.entityData.get(MOUSE_SPEED_X) * (Float)this.entityData.get(PROPELLER_ROT));
            }

            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = this.upInputDown || this.forwardInputDown;
                    boolean down = this.downInputDown;
                    if (!this.engineStart && up) {
                        this.engineStart = true;
                        this.level().playSound((Player)null, this, (SoundEvent)ModSounds.HELICOPTER_ENGINE_START.get(), this.getSoundSource(), 3.0F, 1.0F);
                    }

                    if (up && this.engineStartOver) {
                        ++this.holdPowerTick;
                        this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 7.0E-4F * (float)Math.min(this.holdPowerTick, 10), 0.12F));
                    }

                    if (this.engineStartOver) {
                        if (down) {
                            ++this.holdPowerTick;
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.025F));
                        } else if (this.backInputDown) {
                            ++this.holdPowerTick;
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.052F));
                            if (passenger != null) {
                                passenger.setXRot(0.8F * passenger.getXRot());
                            }
                        }
                    }

                    if (this.engineStart && !this.engineStartOver) {
                        this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 0.0012F, 0.045F));
                    }

                    if (!up && !down && !this.backInputDown && this.engineStartOver) {
                        if (this.getDeltaMovement().y() < (double)0.0F) {
                            this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 2.0E-4F, 0.12F));
                        } else {
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - (this.onGround() ? 5.0E-5F : 2.0E-4F), 0.0F));
                        }

                        this.holdPowerTick = 0;
                    }
                } else {
                    this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 1.0E-4F, 0.0F));
                    this.forwardInputDown = false;
                    this.backInputDown = false;
                    this.engineStart = false;
                    this.engineStartOver = false;
                }
            }
        } else if (!this.onGround() && this.engineStartOver) {
            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 3.0E-4F, 0.01F));
            this.destroyRot += 0.08F;
            float diffX = 45.0F - this.getXRot();
            float diffZ = -20.0F - this.getRoll();
            this.setXRot(this.getXRot() + diffX * 0.05F * (Float)this.entityData.get(PROPELLER_ROT));
            this.setYRot(this.getYRot() + this.destroyRot);
            this.setZRot(this.getRoll() + diffZ * 0.1F * (Float)this.entityData.get(PROPELLER_ROT));
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, (double)(-this.destroyRot) * 0.004, (double)0.0F));
        }

        if ((Boolean)this.entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.98F);
        }

        this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * 0.9F);
        this.entityData.set(PROPELLER_ROT, Mth.lerp(0.18F, (Float)this.entityData.get(PROPELLER_ROT), (Float)this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30.0F * (Float)this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, (Float)this.entityData.get(PROPELLER_ROT) * 0.9995F);
        if (this.engineStart) {
            this.consumeEnergy((int)((double)(Integer)DragonRiseServerConfig.ZHI10ME_MIN_ENERGY_COST.get() + (double)(Float)this.entityData.get(POWER) * ((double)((Integer)DragonRiseServerConfig.ZHI10ME_MAX_ENERGY_COST.get() - (Integer)DragonRiseServerConfig.ZHI10ME_MIN_ENERGY_COST.get()) / 0.12)));
        }

        Matrix4f transform = this.getVehicleTransform(1.0F);
        Vector4f force0 = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f force1 = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
        Vec3 force = (new Vec3((double)force0.x, (double)force0.y, (double)force0.z)).vectorTo(new Vec3((double)force1.x, (double)force1.y, (double)force1.z));
        this.setDeltaMovement(this.getDeltaMovement().add(force.scale((double)(Float)this.entityData.get(PROPELLER_ROT))));
        if ((Float)this.entityData.get(POWER) > 0.04F) {
            this.engineStartOver = true;
        }

        if ((Float)this.entityData.get(POWER) < 4.0E-4F) {
            this.engineStart = false;
            this.engineStartOver = false;
        }

    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.HELICOPTER_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return (Float)this.entityData.get(PROPELLER_ROT) * 2.0F;
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
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        }

    }

    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0F;
            float y = -0.5F;
            float z = 1.0F;
            y += (float)passenger.getMyRidingOffset();
            int i = this.getOrderedPassengers().indexOf(passenger);
            if (i == 0) {
                Vector4f worldPosition = this.transformPosition(transform, x, (float) (y-0.1), (float) (z+0.4));
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            } else if (i == 1) {
                Vector4f worldPosition = this.transformPosition(transform, -x, (float) (y+0.5), z-1);
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            }

            if (passenger != this.getFirstPassenger()) {
                passenger.setXRot(passenger.getXRot() + (this.getXRot() - this.xRotO));
            }

            this.copyEntityData(passenger);
        }
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
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(this.getYRot());
        }

    }

    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo + (double)1.45F, this.getY() + (double)1.45F), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, this.yRotO, this.getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, this.xRotO, this.getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    //炮手炮塔矩阵
    public Matrix4f getGunTransform(float ticks) {
        Matrix4f transformT = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0F, -0.85F, 2.6F - (float)ClientMouseHandler.custom3pDistanceLerp);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, gunYRotO, getGunYRot()) - Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformT;
    }
    public Matrix4f getGunBaseTransform(float ticks) {
        Matrix4f transformT = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0F, -0.85F, 2.6F - (float)ClientMouseHandler.custom3pDistanceLerp);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
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
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getAttacker()), 300.0F, this.getX(), this.getY(), this.getZ(), 8.0F, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? BlockInteraction.DESTROY : BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
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
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f worldPosition2 = this.transformPosition(transform, 0.0F, 0.01F, 1.0F);
        return (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
    }

    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity var6 = this.getNthEntity(i);
            if (var6 instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }
        //主
        if(type == 0) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            if (this.getWeaponIndex(0) == 0 && (Integer) this.getEntityData().get(LOADED_ROCKET) > 0) {
                SmallRocketEntity heliRocketEntity = ((SmallRocketWeapon) this.getWeapon(0)).create(player);
                Vector4f worldPosition;
                Vector4f worldPosition2;
                if (this.fireIndex == 0) {
                    worldPosition = this.transformPosition(transform, 1.7F, -0.83F, 0.8F);
                    worldPosition2 = this.transformPosition(transform, 1.7064999F, -0.81799996F, 1.8F);
                    this.fireIndex = 1;
                } else {
                    worldPosition = this.transformPosition(transform, -1.7F, -0.83F, 0.8F);
                    worldPosition2 = this.transformPosition(transform, -1.6885F, -0.81799996F, 1.8F);
                    this.fireIndex = 0;
                }

                Vec3 shootVec = (new Vec3((double) worldPosition.x, (double) worldPosition.y, (double) worldPosition.z)).vectorTo(new Vec3((double) worldPosition2.x, (double) worldPosition2.y, (double) worldPosition2.z)).normalize();
                heliRocketEntity.setPos((double) worldPosition.x, (double) worldPosition.y, (double) worldPosition.z);
                heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7.0F, 0.25F);
                player.level().addFreshEntity(heliRocketEntity);
                if (!player.level().isClientSide) {
                    this.playShootSound3p(player, 0, 6, 6, 6);
                }

                this.entityData.set(LOADED_ROCKET, (Integer) this.getEntityData().get(LOADED_ROCKET) - 1);
                this.reloadCoolDown = 30;
            }
        }
        if (type == 1) {
            if (this.cannotFire | this.getEntityData().get(MG_AMMO)==0) return;
            Matrix4f transform = getGunnerBarrelTransform(1);
            Vector4f worldPosition = transformPosition(transform, 0F, -0.15F, 0);

            SmallCannonShellEntity jipaoentity = ((SmallCannonShellWeapon) getWeapon(1)).create(player);

            jipaoentity.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
            double rand1 = Math.random()/30;
            double rand2 = Math.random()/30;
            double rand3 = Math.random()/30;
            jipaoentity.shoot(getGunnerVector(1).x+rand1, getGunnerVector(1).y + 0.01f+rand2, getGunnerVector(1).z+rand3, 20, 0.3f);

            this.level().addFreshEntity(jipaoentity);
            sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, worldPosition.x, worldPosition.y, worldPosition.z, 1, 0, 0, 0, 0, false);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 1, 4, 12, 24);
            }

            this.entityData.set(GUNFINETIME1, 2);
            this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);

            ShakeClientMessage.sendToNearbyPlayers(this, 4, 6, 4, 6);

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

    }

    public int mainGunRpm(Player player) {
        return 500;
    }

    public boolean canShoot(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return this.entityData.get(AMMO) > 0;
            }
        }
        if (player == getNthEntity(1)) {
            if (getWeaponIndex(1)==0){
                return this.entityData.get(MG_AMMO) > 0;
            }
        }
        return false;
    }

    public int getAmmoCount(Player player) {
        if (player == getNthEntity(1)) {
            return this.entityData.get(MG_AMMO);
        }
        if (player == getNthEntity(0)) {
            return (Integer) this.entityData.get(AMMO);
        }
        return 0;
    }

    public boolean hidePassenger(Entity entity) {
        return false;
    }

    public int zoomFov() {
        return 3;
    }

    public int getWeaponHeat(Player player) {
        return (Integer)this.entityData.get(HEAT);
    }

    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    public float getPower() {
        return (Float)this.entityData.get(POWER);
    }

    public int getDecoy() {
        return (Integer)this.entityData.get(DECOY_COUNT);
    }

    public int getMaxPassengers() {
        return 2;
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/Z10_icon.png");
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return seatIndex == 0 ? (double)0.0F : original;
    }

    public double getMouseSensitivity() {
        return (double)0.25F;
    }

    public double getMouseSpeedX() {
        return 0.3;
    }

    public double getMouseSpeedY() {
        return (double)0.15F;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        if (this.getSeatIndex(entity) == 2) {
            return Pair.of(Axis.XP.rotationDegrees(-this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(this.getViewXRot(tickDelta)));
        } else {
            return this.getSeatIndex(entity) == 3 ? Pair.of(Axis.XP.rotationDegrees(this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(-this.getViewXRot(tickDelta))) : Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
        }
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo + (double)1.45F, this.getY() + (double)1.45F), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees((float)((double)(-Mth.lerp(ticks, this.yRotO, this.getYRot())) + ClientMouseHandler.freeCameraYaw)));
        transform.rotate(Axis.XP.rotationDegrees((float)((double)Mth.lerp(ticks, this.xRotO, this.getXRot()) + ClientMouseHandler.freeCameraPitch)));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    //视角旋转
    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 1) {
            return new Vec2((float) -getYRotFromVector(this.getGunnerVector(partialTicks)), (float) -getXRotFromVector(this.getGunnerVector(partialTicks)));
        }
        return this.getSeatIndex(player) == 0 ? new Vec2((float)((double)this.getRotY(partialTicks) - ClientMouseHandler.freeCameraYaw), (float)((double)this.getRotX(partialTicks) + ClientMouseHandler.freeCameraPitch)) : super.getCameraRotation(partialTicks, player, false, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean useFixedCameraPos(Entity entity) {
        return this.getSeatIndex(entity) == 1;
    }

    //摄像机位置
    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        Matrix4f transform = this.getClientVehicleTransform(partialTicks);
        if (this.getSeatIndex(player) == 0) {
            Vector4f maxCameraPosition = this.transformPosition(transform, -2.1F, 1.0F, -10.0F - (float)ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);
            return isFirstPerson ? new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ())) : finalPos;
        }else if (this.getSeatIndex(player) == 1) {
            transform = this.getGunBaseTransform(partialTicks);
            Vector4f maxCameraPosition = this.transformPosition(transform, 0,0.7f,1.2f);
            if(isFirstPerson) {
                if (zoom){
                    return CameraTool.getMaxZoom(transform, maxCameraPosition);
                }
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
            }
            return CameraTool.getMaxZoom(transform, maxCameraPosition);
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/aircraft.png");
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7);
    }

    public void updateOBB() {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.41874993F, -0.15625F);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition2 = this.transformPosition(transform, 0.0F, 0.049999952F, 1.90625F);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition3 = this.transformPosition(transform, 0.0F, 0.86249995F, -4.1875F);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition4 = this.transformPosition(transform, -0.125F, 0.89374995F, -6.34375F);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition5 = this.transformPosition(transform, -0.125F, 2.1125F, -6.65625F);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition6 = this.transformPosition(transform, 0.0F, 1.83125F, -0.53125F);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1.0F, this));
        Vector4f worldPosition7 = this.transformPosition(transform, 0.1875F, 0.64374995F, -6.15625F);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1.0F, this));
    }

    static {
        PROPELLER_ROT = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.FLOAT);
        LOADED_ROCKET = SynchedEntityData.defineId(ZHI10MEEntity.class, EntityDataSerializers.INT);
    }
}
