package com.modernwarfare.globalstorm.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.modernwarfare.globalstorm.GlobalStorm;
import com.modernwarfare.globalstorm.resource.point.PointGroupData;
import com.modernwarfare.globalstorm.resource.tools.JsonTool;
import org.apache.logging.log4j.Marker;

import java.io.Console;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListJsonDataManager<T> {

    protected final Map<String, List<T>> dataMap = Maps.newHashMap();

    private final Gson gson;
    private final Type dataListType;
    //private final Class<T> dataClass;
    private final Marker marker;

    public ListJsonDataManager(Gson gson, Class<T> dataClass, Marker marker) {
        this.gson = gson;
        this.dataListType = TypeToken.getParameterized(List.class, dataClass).getType();
        this.marker = marker;
    }

    public ListJsonDataManager(Gson gson, Type dataListType, Marker marker) {
        this.gson = gson;
        this.dataListType = dataListType;
        this.marker = marker;
    }

    public Map<String,JsonElement> loadJson(Path path) {
        return JsonTool.getAllJsonByPath(path, null);
    };

    public Map<String,JsonElement> loadJson(Path path, String name) {
        Map<String,JsonElement> map = new HashMap<>();
        map.put(name,JsonTool.getJsonByPath(path, name, null));
        return map;
    };


    public void apply(Map<String, JsonElement> pJsonElements) {
        dataMap.clear();
        for (Map.Entry<String, JsonElement> entry : pJsonElements.entrySet()) {
            String id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                List<T> data = parseJson(element);
                if (data != null) {
                    data.forEach(t -> {
                        GlobalStorm.LOGGER.debug(marker, "Adding data: " + t.getClass());
                    });
                    dataMap.put(id, data);
                }
            }catch (JsonParseException | IllegalArgumentException e) {
                GlobalStorm.LOGGER.error(marker,"Error parsing JSON from {}",id, e);
            }

        }

    }

    protected List<T> parseJson(JsonElement element) {
        //Type listType = new TypeToken<List<PointGroupData>>(){}.getType();
        //List<T> output = gson.fromJson(element, listType);
        List<T> output = gson.fromJson(element, dataListType);
        return output;
    }

//    public Class<T> getDataClass() {
//        return dataClass;
//    }

    public Marker getMarker() {
        return marker;
    }

    public Gson getGson() {
        return gson;
    }

    public List<T> getData(String id) {
        return dataMap.get(id);
    }

    public Map<String, List<T>> getAllData() {
        return dataMap;
    }
}
