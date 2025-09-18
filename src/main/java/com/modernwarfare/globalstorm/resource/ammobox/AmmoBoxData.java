package com.modernwarfare.globalstorm.resource.ammobox;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class AmmoBoxData {

    @SerializedName("gun_id")
    private ResourceLocation id;

    private int count;

    public AmmoBoxData() {
        // 默认构造函数
    }

    public AmmoBoxData(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
    }

    // Getter 和 Setter 方法
    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "AmmoBoxEntry{" +
                "id='" + id + '\'' +
                ", count=" + count +
                '}';
    }
}
