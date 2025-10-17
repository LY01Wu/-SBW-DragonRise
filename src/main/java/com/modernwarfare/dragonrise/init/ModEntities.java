package com.modernwarfare.dragonrise.init;

import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.AKD9Enity;
import com.modernwarfare.dragonrise.entity.ZHI10MEEntity;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ENTITY_TYPES, Mod.MODID);

    public static final RegistryObject<EntityType<ZTZ99AEntity>> ZTZ99A =
            REGISTRY.register("ztz99a", () ->
                    EntityType.Builder.<ZTZ99AEntity>of(ZTZ99AEntity::new, MobCategory.MISC)
                            .setTrackingRange(64)
                            .setUpdateInterval(2)
                            .setCustomClientFactory(ZTZ99AEntity::new)
                            .fireImmune()
                            .sized(4.0f, 2.9f)
                            .build("ztz99a")
            );

    public static final RegistryObject<EntityType<ZHI10MEEntity>> ZHI10ME = register("zhi10me",
            EntityType.Builder.<ZHI10MEEntity>of(ZHI10MEEntity::new,MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(ZHI10MEEntity::new)
                    .fireImmune()
                    .sized(1.4f,2.9f)
    );

    public static final RegistryObject<EntityType<AKD9Enity>> AKD9 =
            REGISTRY.register("akd9", () ->
                    EntityType.Builder.<AKD9Enity>of(AKD9Enity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("akd9")
            );
//    public static final RegistryObject<EntityType<AbstractVehicleEntity>> VEHICLE = register("vehicle",
//            EntityType.Builder.<AbstractVehicleEntity>of(AbstractVehicleEntity::new, MobCategory.MISC)
//                   .setTrackingRange(64)
//                   .setUpdateInterval(1)
//                   .setCustomClientFactory(AbstractVehicleEntity::new)
//                   .fireImmune()
//                   .sized(1.0f, 1.0f)
//            );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }
}
