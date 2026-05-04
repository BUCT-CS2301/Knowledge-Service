package com.entity;

import com.service.exception.ProductNotFoundException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ProductView extends Product  {
    private ArrayList<CommentView> commentView;
    private int if_collect;
    private List<Cart> recommend;
    public ProductView(ArrayList<CommentView> commentView) {
        this.commentView = commentView;
    }

    public ProductView(BigInteger id, String bibliography, String credit, String dimensions, String geography, String img_url, String label, String medium, String object_name, String object_type, String previous_owner, String provenance, String time_period, String url, String cat1, String cat2, String cat3, String makers_job, String makers_name, String makers_born, String relics_id, String museum, String object_id, ArrayList<CommentView> commentView, int if_collect) {
        super(id, bibliography, credit, dimensions, geography, img_url, label, medium, object_name, object_type, previous_owner, provenance, time_period, url, cat1, cat2, cat3, makers_job, makers_name, makers_born, relics_id, museum, object_id);
        this.commentView = commentView;
        this.if_collect = if_collect;
    }

    public ProductView() {

    }

    public List<Cart> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<Cart> recommend) {
        this.recommend = recommend;
    }

    public ArrayList<CommentView> getCommentView() {
        return commentView;
    }

    public void setCommentView(ArrayList<CommentView> commentView) {
        this.commentView = commentView;
    }

    public int getIf_collect() {
        return if_collect;
    }

    public void setIf_collect(int if_collect) {
        this.if_collect = if_collect;
    }

    @Override
    public String toString() {
        return "ProductView{" +
                "commentView=" + commentView +
                '}';
    }
}
