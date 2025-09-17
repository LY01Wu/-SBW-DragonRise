package com.modernwarfare.dragonrise.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class DragonRiseServerConfig {
    
    public static ForgeConfigSpec.IntValue ZTZ99A_ENERGY_COST;
    public static ForgeConfigSpec.IntValue ZTZ99A_CANNON_COOLDOWN;
    public static ForgeConfigSpec.IntValue ZTZ99A_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue ZTZ99A_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZTZ99A_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue ZTZ99A_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue ZTZ99A_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZTZ99A_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue ZHI10ME_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue ZHI10ME_MAX_ENERGY_COST;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_CANNON_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_ROCKET_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ZHI10ME_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue ZHI10ME_CANNON_DESTROY;


    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("vehicle");

        builder.push("ZTZ-99A");

        builder.comment("The energy cost of ZTZ-99A per tick");
        ZTZ99A_ENERGY_COST = builder.defineInRange("ZTZ99A_energy_cost", 128, 0, 2147483647);

        builder.comment("The cannon cooldown of ZTZ-99A");
        ZTZ99A_CANNON_COOLDOWN = builder.defineInRange("ZTZ99A_cannon_cooldown", 120, 1, 10000000);

        builder.comment("The cannon damage of ZTZ-99A");
        ZTZ99A_AP_CANNON_DAMAGE = builder.defineInRange("ZTZ99A_ap_cannon_damage", 500, 1, 10000000);

        builder.comment("The cannon explosion damage of ZTZ-99A");
        ZTZ99A_AP_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("ZTZ99A_ap_cannon_explosion_damage", 100, 1, 10000000);

        builder.comment("The cannon explosion radius of ZTZ-99A");
        ZTZ99A_AP_CANNON_EXPLOSION_RADIUS = builder.defineInRange("ZTZ99A_ap_cannon_explosion_radius", 4d, 1d, 10000000d);

        builder.comment("The cannon damage of ZTZ-99A");
        ZTZ99A_HE_CANNON_DAMAGE = builder.defineInRange("ZTZ99A_he_cannon_damage", 150, 1, 10000000);

        builder.comment("The cannon explosion damage of ZTZ-99A");
        ZTZ99A_HE_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("ZTZ99A_he_cannon_explosion_damage", 150, 1, 10000000);

        builder.comment("The cannon explosion radius of ZTZ-99A");
        ZTZ99A_HE_CANNON_EXPLOSION_RADIUS = builder.defineInRange("ZTZ99A_he_cannon_explosion_radius", 10d, 1d, 10000000d);

        builder.pop();

        builder.push("ZHI10ME");
        builder.comment("The min energy cost of AH-6 per tick");
        ZHI10ME_MIN_ENERGY_COST = builder.defineInRange("ah_6_min_energy_cost", 64, 0, Integer.MAX_VALUE);
        builder.comment("The max energy cost of AH-6 per tick");
        ZHI10ME_MAX_ENERGY_COST = builder.defineInRange("ah_6_max_energy_cost", 128, 0, Integer.MAX_VALUE);
        builder.comment("The cannon damage of AH-6");
        ZHI10ME_CANNON_DAMAGE = builder.defineInRange("ah_6_cannon_damage", (double)25.0F, (double)1.0F, (double)1.0E7F);
        builder.comment("The cannon explosion damage of AH-6");
        ZHI10ME_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("ah_6_cannon_explosion_damage", (double)12.0F, (double)1.0F, (double)1.0E7F);
        builder.comment("The cannon explosion radius of AH-6");
        ZHI10ME_CANNON_EXPLOSION_RADIUS = builder.defineInRange("ah_6_cannon_explosion_radius", (double)3.5F, (double)1.0F, (double)1.0E7F);
        builder.comment("The rocket damage of AH-6");
        ZHI10ME_ROCKET_DAMAGE = builder.defineInRange("ah_6_rocket_damage", (double)80.0F, (double)1.0F, (double)1.0E7F);
        builder.comment("The rocket explosion damage of AH-6");
        ZHI10ME_ROCKET_EXPLOSION_DAMAGE = builder.defineInRange("ah_6_rocket_explosion_damage", (double)40.0F, (double)1.0F, (double)1.0E7F);
        builder.comment("The rocket explosion radius of AH-6");
        ZHI10ME_ROCKET_EXPLOSION_RADIUS = builder.defineInRange("ah_6_rocket_explosion_radius", (double)5.0F, (double)1.0F, (double)1.0E7F);
        builder.comment("Whether to destroy the block when cannon of AH-6 hits a block");
        ZHI10ME_CANNON_DESTROY = builder.define("ah_6_cannon_destroy", true);
        builder.pop();

        builder.pop();
    }
        
    
}
