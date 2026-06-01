package com.entity;

import java.io.Serializable;

public class Collect implements Serializable {
    private Integer id;
    private Integer uid;
    private Integer rid;
    private String relicObjectId;
    private String relicName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getRelicObjectId() {
        return relicObjectId;
    }

    public void setRelicObjectId(String relicObjectId) {
        this.relicObjectId = relicObjectId;
    }

    public String getRelicName() {
        return relicName;
    }

    public void setRelicName(String relicName) {
        this.relicName = relicName;
    }

    @Override
    public String toString() {
        return "Collect{" +
                "id=" + id +
                ", uid=" + uid +
                ", rid=" + rid +
                '}';
    }
}
