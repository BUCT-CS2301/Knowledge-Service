package com.entity;

import java.io.Serializable;

public class CollectView implements Serializable {
    Integer id;//收藏id
    Integer uid;//用户id
    Integer rid;//文物id
    String username;//用户名
    String relicname;//文物id

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRelicname() {
        return relicname;
    }

    public void setRelicname(String relicname) {
        this.relicname = relicname;
    }

    @Override
    public String toString() {
        return "CollectView{" +
                "id=" + id +
                ", uid=" + uid +
                ", rid=" + rid +
                ", username='" + username + '\'' +
                ", relicname='" + relicname + '\'' +
                '}';
    }
}
