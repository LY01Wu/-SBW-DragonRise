package com.modernwarfare.globalstorm.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.modernwarfare.globalstorm.GlobalStorm;
import com.modernwarfare.globalstorm.resource.tools.JsonTool;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Marker;

import java.nio.file.Path;
import java.util.Map;

public class JsonDataManager<T> {
    protected final Map<String, T> dataMap = Maps.newHashMap();

    private final Gson gson;
    private final Class<T> dataClass;
    private final Marker marker;

    public JsonDataManager(Gson gson, Class<T> dataClass, Marker marker) {
        this.gson = gson;
        this.dataClass = dataClass;
        this.marker = marker;
    }

    public Map<String,JsonElement> loadJson(Path path,String fileType) {
        return JsonTool.getAllJsonByPath(path, fileType);
    };

    public void apply(Map<String, JsonElement> pJsonElements) {
        dataMap.clear();
        for (Map.Entry<String, JsonElement> entry : pJsonElements.entrySet()) {
            String id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                T data = parseJson(element);
                if (data != null) {
                    dataMap.put(id, data);
                }
            }catch (JsonParseException | IllegalArgumentException e) {
                GlobalStorm.LOGGER.error(marker,"Error parsing JSON from {}",id, e);
            }

        }

    }

    protected T parseJson(JsonElement element) {
        return gson.fromJson(element, getDataClass());
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public Marker getMarker() {
        return marker;
    }

    public Gson getGson() {
        return gson;
    }

    public T getData(String id) {
        return dataMap.get(id);
    }

    public Map<String, T> getAllData() {
        return dataMap;
    }
}
