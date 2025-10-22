package com.modernwarfare.dragonrise.init;


import com.modernwarfare.dragonrise.Mod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DRModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Mod.MODID);

    public static final RegistryObject<SoundEvent> ZTZ99A_RELOAD = REGISTRY.register("ztz99a_reload", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_reload")));
    //public static final RegistryObject<SoundEvent> ZTZ99A_CARTRIDGE = REGISTRY.register("ztz99a_cartridge", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_cartridge")));
    public static final RegistryObject<SoundEvent> ZTZ99A_FIRE_1P = REGISTRY.register("ztz99a_fire_1p", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_fire_1p")));
    public static final RegistryObject<SoundEvent> ZTZ99A_FIRE_3P = REGISTRY.register("ztz99a_fire_3p", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_fire_3p")));
    public static final RegistryObject<SoundEvent> QJY88_SHOT_1P = REGISTRY.register("qjy88_shoot", () -> SoundEvent.createVariableRangeEvent(Mod.loc("qjy88_shoot")));
    public static final RegistryObject<SoundEvent> QJY88_SHOT_3P = REGISTRY.register("qjy88_shoot_3p", () -> SoundEvent.createVariableRangeEvent(Mod.loc("qjy88_shoot_3p")));
    public static final RegistryObject<SoundEvent> ZBD04A_RELOAD = REGISTRY.register("zbd04a_reload", () -> SoundEvent.createVariableRangeEvent(Mod.loc("zbd04a_reload")));
    //public static final RegistryObject<SoundEvent> ZTZ99A_FAR = REGISTRY.register("ztz99a_far", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_far")));
    //public static final RegistryObject<SoundEvent> ZTZ99A_VERYFAR = REGISTRY.register("ztz99a_veryfar", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_veryfar")));
    //public static final RegistryObject<SoundEvent> ZTZ99A_ENGINE = REGISTRY.register("ztz99a_engine", () -> SoundEvent.createVariableRangeEvent(Mod.loc("ztz99a_engine")));
}
