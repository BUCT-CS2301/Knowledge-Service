package com.entity;

import java.io.Serializable;

public class Comment extends BaseC implements Serializable {
    private Integer cid;//评论id
    private String content;//评论内容
    private Integer rid;//文物id
    private Integer uid;//用户id
    private Integer user_comment;//用户评论权限
    public Integer getUid() {
        return uid;
    }

    public String getContent() {
        return content;
    }

    public Integer getRid() {
        return rid;
    }

    public Integer getCid() {
        return cid;
    }

    public Integer getUser_comment() {
        return user_comment;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setContent(String cotent) {
        this.content = cotent;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public void setUser_comment(Integer user_comment) {
        this.user_comment = user_comment;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "cid=" + cid +
                ", content='" + content + '\'' +
                ", rid=" + rid +
                ", uid=" + uid +
                ", user_comment=" + user_comment +
                '}';
    }
}
