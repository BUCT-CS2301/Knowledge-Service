package com.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
@Data
public class Product  {
    private BigInteger id;
    private String bibliography;//参考文献
    private String credit;//捐赠信息
    private String dimensions;//规格
    private String geography;//产地
    private String img_url;//图片
    private String label;//作者简介故事
    private String medium;//材质
    private String object_name;//文物姓名
    private String object_type;//展区
    private String previous_owner;//前拥有者
    private String provenance;//出处
    private String time_period;//具体时期
    private String url;//原本文物详情链接
    private String cat1;//材质，分类查询
    private String cat2;//朝代里的拓展，分类查询，时间轴数据
    private String cat3;//分类，分类查询
    private String makers_job;//制作者工作
    private String makers_name;//制作者名字
    private String makers_born;//制作者出生
    private String relics_id;
    private String museum;
    private String object_id;

    public Product() {
    }

    public Product(BigInteger id, String bibliography, String credit, String dimensions, String geography, String img_url, String label, String medium, String object_name, String object_type, String previous_owner, String provenance, String time_period, String url, String cat1, String cat2, String cat3, String makers_job, String makers_name, String makers_born, String relics_id, String museum, String object_id) {
        this.id = id;
        this.bibliography = bibliography;
        this.credit = credit;
        this.dimensions = dimensions;
        this.geography = geography;
        this.img_url = img_url;
        this.label = label;
        this.medium = medium;
        this.object_name = object_name;
        this.object_type = object_type;
        this.previous_owner = previous_owner;
        this.provenance = provenance;
        this.time_period = time_period;
        this.url = url;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.makers_job = makers_job;
        this.makers_name = makers_name;
        this.makers_born = makers_born;
        this.relics_id = relics_id;
        this.museum = museum;
        this.object_id = object_id;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMakers_job() {
        return makers_job;
    }

    public void setMakers_job(String makers_job) {
        this.makers_job = makers_job;
    }

    public String getMakers_name() {
        return makers_name;
    }

    public void setMakers_name(String makers_name) {
        this.makers_name = makers_name;
    }

    public String getMakers_born() {
        return makers_born;
    }

    public void setMakers_born(String makers_born) {
        this.makers_born = makers_born;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getPrevious_owner() {
        return previous_owner;
    }

    public void setPrevious_owner(String previous_owner) {
        this.previous_owner = previous_owner;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getTime_period() {
        return time_period;
    }

    public void setTime_period(String time_period) {
        this.time_period = time_period;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getMuseum() {
        return museum;
    }

    public void setMuseum(String museum) {
        this.museum = museum;
    }

    public String getRelics_id() {
        return relics_id;
    }

    public void setRelics_id(String relics_id) {
        this.relics_id = relics_id;
    }



    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", bibliography='" + bibliography + '\'' +
                ", credit='" + credit + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", geography='" + geography + '\'' +
                ", img_url='" + img_url + '\'' +
                ", label='" + label + '\'' +
                ", medium='" + medium + '\'' +
                ", object_name='" + object_name + '\'' +
                ", object_type='" + object_type + '\'' +
                ", previous_owner='" + previous_owner + '\'' +
                ", provenance='" + provenance + '\'' +
                ", time_period='" + time_period + '\'' +
                ", url='" + url + '\'' +
                ", cat1='" + cat1 + '\'' +
                ", cat2='" + cat2 + '\'' +
                ", cat3='" + cat3 + '\'' +
                ", makers_job='" + makers_job + '\'' +
                ", makers_name='" + makers_name + '\'' +
                ", makers_born='" + makers_born + '\'' +
                ", relics_id='" + relics_id + '\'' +
                ", museum='" + museum + '\'' +
                ", object_id='" + object_id + '\'' +
                '}';
    }
}
