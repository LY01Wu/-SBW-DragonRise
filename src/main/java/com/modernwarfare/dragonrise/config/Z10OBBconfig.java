package com.modernwarfare.dragonrise.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Z10OBBconfig implements IConfig{
    Map<String,Map<String,Float>> obb1 = getOBB("obb1");
    Map<String,Map<String,Float>> obb2 = getOBB("obb2");
    Map<String,Map<String,Float>> obb3 = getOBB("obb3");
    Map<String,Map<String,Float>> obb4 = getOBB("obb4");
    Map<String,Map<String,Float>> obb5 = getOBB("obb5");
    Map<String,Map<String,Float>> obb6 = getOBB("obb6");
    Map<String,Map<String,Float>> obb7 = getOBB("obb7");
    Map<String,Map<String,Float>> obb8 = getOBB("obb8");
    Map<String,Map<String,Float>> obb9 = getOBB("obb9");
    @Override
    public void validate() {

    }
    @Override
    public void onUpdate() {

    }
    private Map<String,Map<String,Float>> getOBB(String name){
        return new HashMap<String,Map<String,Float>>(
                Map.of("Vector3f",Map.of("x", 0.0f,"y", 0.0f,"z", 0.0f),
                        "transform",Map.of("x", 0.0f,"y", 0.0f,"z", 0.0f)
                )
        );
    }
    public static final ConfigHandler<Z10OBBconfig> HANDLER = ConfigHandler.of(Z10OBBconfig.class, ".Z10.json");
}
