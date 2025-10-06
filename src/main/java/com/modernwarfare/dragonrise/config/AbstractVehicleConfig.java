package com.modernwarfare.dragonrise.config;

import java.util.HashMap;
import java.util.Map;

public class AbstractVehicleConfig implements IConfig{
    Map<String, String> config=new HashMap<>(Map.of("type", "AbstractVehicle"));
    @Override
    public void validate() {

    }

    @Override
    public void onUpdate() {

    }
    //批量生成json开放
    private static ConfigHandler[] getOBBHandlers(int num){
        ConfigHandler[] handlers = new ConfigHandler[num];
        for(int i=0;i<num;i++){
            handlers[i] = ConfigHandler.of(AbstractVehicleConfig.class, i +".Z10.json");
        }
        return handlers;
    }

    public static final ConfigHandler<AbstractVehicleConfig> HANDLER = ConfigHandler.of(AbstractVehicleConfig.class, ".json");
}
