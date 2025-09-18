package com.modernwarfare.globalstorm.resource.seralizer;

import com.google.gson.*;
import com.modernwarfare.globalstorm.resource.point.PointData;
import com.modernwarfare.globalstorm.resource.point.PointGroupData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointGroupDataDeserializer implements JsonDeserializer<PointGroupData> {

    @Override
    public PointGroupData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        PointGroupData data = new PointGroupData();

        // 解析 "group" 字段
        data.setName(jsonObject.get("group").getAsString());

        // 解析 "points" 字段（Map<String, PointData>）
        Map<String, PointData> pointsMap = new HashMap<>();
        JsonObject pointsJson = jsonObject.getAsJsonObject("points");
        for (Map.Entry<String, JsonElement> entry : pointsJson.entrySet()) {
            String pointName = entry.getKey();
            PointData pointData = context.deserialize(entry.getValue(), PointData.class);
            pointsMap.put(pointName, pointData);
        }
        data.setPoints(pointsMap);

        // 解析 "links" 字段（List<List<String>>）
        List<List<String>> linksList = new ArrayList<>();
        JsonArray linksJson = jsonObject.getAsJsonArray("links");
        for (JsonElement linkElement : linksJson) {
            JsonArray linkArray = linkElement.getAsJsonArray();
            List<String> link = new ArrayList<>();
            for (JsonElement pointElement : linkArray) {
                link.add(pointElement.getAsString());
            }
            linksList.add(link);
        }
        data.setLinks(linksList);

        return data;
    }
}
