package com.modernwarfare.dragonrise.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.ExplosiveProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.modernwarfare.dragonrise.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class AKD9Entity extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {

    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(AKD9Entity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final DamageModifier DAMAGE_MODIFIER = DamageModifier.createDefaultModifier();

    private float damage = 250f;
    private float explosionDamage = 200f;
    private float explosionRadius = 10f;
    private float gravity = 0f;

    public UUID launcherVehicle;

    public AKD9Entity(EntityType<? extends AKD9Entity> type, Level level) {  // 修改这里
        super(type, level);
        this.noCulling = true;
    }

    public AKD9Entity(LivingEntity entity, Level level, float damage, float explosionDamage, float explosionRadius) {
        this(ModEntities.AKD9.get(), level);  // 使用依赖模组的实体类型
        this.setOwner(entity);
        this.damage = damage;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
        this.durability = 50;
    }

    public AKD9Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.AKD9.get(), world);
    }

    public static AKD9Entity create(LivingEntity player) {
        return new AKD9Entity(player, player.level(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_DAMAGE.get(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE.get(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS.get());
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        amount = DAMAGE_MODIFIER.compute(source, amount);
        this.entityData.set(HEALTH, this.entityData.get(HEALTH) - amount);

        return super.hurt(source, amount);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALTH, 10f);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            this.entityData.set(HEALTH, compound.getFloat("Health"));
        }
        if (compound.contains("Damage")) {
            this.damage = compound.getFloat("Damage");
        }
        if (compound.contains("ExplosionDamage")) {
            this.explosionDamage = compound.getFloat("ExplosionDamage");
        }
        if (compound.contains("Radius")) {
            this.explosionRadius = compound.getFloat("Radius");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Health", this.entityData.get(HEALTH));
        compound.putFloat("Damage", this.damage);
        compound.putFloat("ExplosionDamage", this.explosionDamage);
        compound.putFloat("Radius", this.explosionRadius);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.WIRE_GUIDE_MISSILE.get();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();
            if (hardness != -1) {
                if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
                    if (firstHit) {
                        causeExplode(blockHitResult.getLocation());
                        firstHit = false;
                        Mod.queueServerWork(3, this::discard);
                    }
                    this.level().destroyBlock(resultPos, true);
                }
            } else {
                causeExplode(blockHitResult.getLocation());
                this.discard();
            }
            if (!ExplosionConfig.EXPLOSION_DESTROY.get()) {
                causeExplode(blockHitResult.getLocation());
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (this.getOwner() != null && this.getOwner().getVehicle() != null && entity == this.getOwner().getVehicle())
            return;
        if (this.level() instanceof ServerLevel) {
            if (entity == this.getOwner() || (this.getOwner() != null && entity == this.getOwner().getVehicle()))
                return;
            if (this.getOwner() instanceof LivingEntity living) {
                if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                    living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);

                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
                }
            }

            DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);

            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            causeExplode(result.getLocation());
            this.discard();
        }
    }

    @Override
    public void causeExplode(Vec3 vec3) {
        new CustomExplosion.Builder(this)
                .attacker(this.getOwner())
                .damage(explosionDamage)
                .radius(explosionRadius)
                .position(vec3)
                .withParticleType(ParticleTool.ParticleType.HUGE)
                .explode();
    }

    public void largeTrail() {
        if (level().isClientSide && tickCount > 1) {
            double l = getDeltaMovement().length();
            for (double i = 0; i < l; i += 2) {
                Vec3 startPos = new Vec3(getX(), getY(), getZ());
                Vec3 pos = startPos.add(getDeltaMovement().normalize().scale(-i));
                level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }

    @Override
    public void tick() {

        super.tick();

        // 检查二号位是否有人，如果没人则自爆
//        if (this.launcherVehicle != null && this.tickCount > 5) {
//            // 正确的方式通过UUID获取实体
//            Entity vehicleEntity = null;
//            if (this.level() instanceof ServerLevel serverLevel) {
//                vehicleEntity = serverLevel.getEntity(this.launcherVehicle);
//            }
//
//            if (vehicleEntity instanceof ZHI10MEEntity helicopter) {
//                // 获取二号位乘客（炮手）
//                Entity gunner = helicopter.getNthEntity(1);
//                if (gunner == null) {
//                    // 二号位没人，导弹自爆
//                    if (this.level() instanceof ServerLevel) {
//                        causeExplode(position());
//                    }
//                    this.discard();
//                    return;
//                }
//            } else if (vehicleEntity == null) {
//                // 如果直升机实体不存在（可能被销毁），导弹也自爆
//                if (this.level() instanceof ServerLevel) {
//                    causeExplode(position());
//                }
//                this.discard();
//                return;
//            }
//        }

        largeTrail();

        if (this.tickCount == 1) {
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.xo, this.yo, this.zo, 15, 0.8, 0.8, 0.8, 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.xo, this.yo, this.zo, 10, 0.8, 0.8, 0.8, 0.01, true);
            }
        }
        if (this.tickCount > 2) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.03, 1.03, 1.03));
        }

        if (tickCount > 3 && this.getOwner() != null && this.launcherVehicle != null) {
            // 正确的方式通过UUID获取实体
            Entity vehicleEntity = null;
            if (this.level() instanceof ServerLevel serverLevel) {
                vehicleEntity = serverLevel.getEntity(this.launcherVehicle);
            }

            if (vehicleEntity instanceof ZHI10MEEntity helicopter) {
                // 检查引导者是否为二号位炮手
                Entity gunner = helicopter.getNthEntity(1);
                if (gunner != this.getOwner()) {
                    // 引导者不是二号位炮手，导弹自爆
                    if (this.level() instanceof ServerLevel) {
                        causeExplode(position());
                    }
                    this.discard();
                    return;
                }

                // 从二号位炮手眼睛位置发出引导射线
                // 注意：需要先在ZHI10MEEntity中添加getGunnerEyePosition方法
                Vec3 eyePos = helicopter.getGunnerEyePosition(1.0F);
                Vec3 lookVec = helicopter.getGunnerVector(1.0F).normalize();

                Vec3 toVec = getDeltaMovement().normalize();

                // 计算目标位置
                BlockHitResult result = level().clip(new ClipContext(
                        eyePos,
                        eyePos.add(lookVec.scale(512)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this.getOwner()
                ));
                Vec3 hitPos = result.getLocation();

                toVec = this.position().vectorTo(hitPos).normalize();
                Mod.LOGGER.info("hitPos: " + hitPos);
                setDeltaMovement(getDeltaMovement().add(toVec.scale(0.8)));
                setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.8, 0.8));
            } else {
                // 如果找不到直升机实体，导弹自爆
                if (this.level() instanceof ServerLevel) {
                    causeExplode(position());
                }
                this.discard();
                return;
            }
        }

        if (this.tickCount > 300 || this.isInWater() || this.entityData.get(HEALTH) <= 0) {
            if (this.level() instanceof ServerLevel) {
                causeExplode(position());
            }
            this.discard();
        }
        destroyBlock();
    }

    private PlayState movementPredicate(AnimationState<AKD9Entity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.jvm.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean shouldSyncMotion() {
        return true;
    }

    @Override
    public @NotNull SoundEvent getCloseSound() {
        return ModSounds.ROCKET_ENGINE.get();
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.ROCKET_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.4f;
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void setExplosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
    }

    @Override
    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setLauncherVehicle(UUID uuid) {
        this.launcherVehicle = uuid;
    }

    @Override
    public boolean forceLoadChunk() {
        return true;
    }

    public static AKD9Entity createWithLauncher(LivingEntity player, UUID launcherVehicle) {
        AKD9Entity missile = new AKD9Entity(player, player.level(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_DAMAGE.get(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE.get(),
                ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS.get());
        missile.setLauncherVehicle(launcherVehicle);
        return missile;
    }

}
