package com.modernwarfare.globalstorm.resource.point;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PointGroupData {

    //@SerializedName("group")
    private String name;

    //@SerializedName("points")
    private Map<String,PointData> points;

    //@SerializedName("links")
    private List<List<String>> links;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String,PointData> getPoints() {
        return points;
    }

    public void setPoints(Map<String,PointData> points) {
        this.points = points;
    }

    public List<List<String>> getLinks() {
        return links;
    }

    public void setLinks(List<List<String>> links) {
        this.links = links;
    }
}

