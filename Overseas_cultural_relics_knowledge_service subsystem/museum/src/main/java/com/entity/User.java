package com.entity;

import lombok.Data;

@Data
public class User {
    private int user_id;//用户id
    private String user_password;//用户密码
    private String user_name;//用户名
    private int user_sex;//用户性别
    private String user_tel;//电话
    private int user_comment = 0;//评论权限
    private int user_login = 1;//用户登录权限

    public User() {

    }

    public User(int user_id, String user_password, String user_name, int user_sex, String user_tel, int user_comment, int user_login) {
        this.user_id = user_id;
        this.user_password = user_password;
        this.user_name = user_name;
        this.user_sex = user_sex;
        this.user_tel = user_tel;
        this.user_comment = user_comment;
        this.user_login = user_login;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(int user_sex) {
        this.user_sex = user_sex;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public int getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(int user_comment) {
        this.user_comment = user_comment;
    }

    public int getUser_login() {
        return user_login;
    }

    public void setUser_login(int user_login) {
        this.user_login = user_login;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_password='" + user_password + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_sex=" + user_sex +
                ", user_tel='" + user_tel + '\'' +
                ", user_comment=" + user_comment +
                ", user_login=" + user_login +
                '}';
    }
}
