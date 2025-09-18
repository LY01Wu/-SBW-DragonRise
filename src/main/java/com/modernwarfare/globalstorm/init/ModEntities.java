package com.modernwarfare.globalstorm.init;

import com.modernwarfare.globalstorm.GlobalStorm;
import com.modernwarfare.globalstorm.index.entity.AmmoBoxEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GlobalStorm.MODID);

    public static final RegistryObject<EntityType<AmmoBoxEntity>> AMMO_BOX = register("ammo_box",
            EntityType.Builder.<AmmoBoxEntity>of(AmmoBoxEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(AmmoBoxEntity::new)
                    .fireImmune()
                    .sized(1f, 1f)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }

}
