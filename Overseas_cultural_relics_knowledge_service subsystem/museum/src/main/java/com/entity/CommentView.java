package com.entity;

import java.io.Serializable;
import java.util.Date;

public class CommentView implements Serializable {
    private int cid;//评论id
    private int uid;//用户id
    private int rid;//文物id
    private String username;//用户名
    private String relicname;//文物名
    private String content;//评论内容
    private Date created_time;//评论时间

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
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
        return "CommentView{" +
                "cid=" + cid +
                ", uid=" + uid +
                ", rid=" + rid +
                ", username='" + username + '\'' +
                ", relicname='" + relicname + '\'' +
                ", content='" + content + '\'' +
                ", created_time=" + created_time +
                '}';
    }
}
