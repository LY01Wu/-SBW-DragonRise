package com.modernwarfare.dragonrise.config;

import com.modernwarfare.dragonrise.config.server.DragonRiseServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DragonRiseServerConfig.init(builder);

        return builder.build();
    }
}
