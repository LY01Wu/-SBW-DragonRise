package com.modernwarfare.dragonrise.init;

import com.modernwarfare.dragonrise.Mod;
import com.modernwarfare.dragonrise.entity.ZHI10MEEntity;
import com.modernwarfare.dragonrise.entity.ZTZ99AEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Mod.MODID);

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
