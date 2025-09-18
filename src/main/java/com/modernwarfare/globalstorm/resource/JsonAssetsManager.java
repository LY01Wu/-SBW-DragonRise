package com.modernwarfare.globalstorm.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.modernwarfare.globalstorm.resource.point.PointGroupData;
import com.modernwarfare.globalstorm.resource.seralizer.PointGroupDataDeserializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class JsonAssetsManager {
    private static JsonAssetsManager INSTANCE = new JsonAssetsManager();

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PointGroupData.class, new PointGroupDataDeserializer())
            .create();

    private ListJsonDataManager<PointGroupData> pointData;
    private static final Path pointRoot = FMLPaths.GAMEDIR.get().resolve("CPoints");

    public void reloadData() {
        pointData = new ListJsonDataManager<>(GSON, PointGroupData.class, null);
        pointData.apply(pointData.loadJson(pointRoot,null));
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        var jsonAssetsManager = new JsonAssetsManager();
        jsonAssetsManager.reloadData();
        INSTANCE = jsonAssetsManager;
    }

    public static JsonAssetsManager getINSTANCE() {
        return INSTANCE;
    }

    public ListJsonDataManager<PointGroupData> getPointData() {
        return pointData;
    }
}
