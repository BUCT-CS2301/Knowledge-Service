package com.entity;

import lombok.Data;

import java.math.BigInteger;
@Data
public class Cart {
    private BigInteger id;
    private String object_name;//文物姓名
    private String cat1;//材质
    private String cat2;//朝代
    private String cat3;//分类
    private String makers_name;//作者姓名
    private String img_url = null;
    public Cart() {
    }

    public Cart(BigInteger id, String object_name, String cat1, String cat2, String cat3, String makers_name, String img_url) {
        this.id = id;
        this.object_name = object_name;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.makers_name = makers_name;
        this.img_url = img_url;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public String getMakers_name() {
        return makers_name;
    }

    public void setMakers_name(String makers_name) {
        this.makers_name = makers_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", object_name='" + object_name + '\'' +
                ", cat1='" + cat1 + '\'' +
                ", cat2='" + cat2 + '\'' +
                ", cat3='" + cat3 + '\'' +
                ", makers_name='" + makers_name + '\'' +
                ", img_url='" + img_url + '\'' +
                '}' + "\n";
    }
}
