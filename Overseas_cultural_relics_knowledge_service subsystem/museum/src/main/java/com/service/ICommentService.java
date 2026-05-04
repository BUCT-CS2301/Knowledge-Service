package com.service;

import com.entity.CommentView;

import java.util.ArrayList;

public interface ICommentService {
    //写评论
    Integer notecomment(Integer uid, Integer rid,String text);
    //删除评论
    Integer deletecomment(Integer cid);
    ArrayList<CommentView> viewcommentfrelics(Integer rid);//看当前文物的评论
    ArrayList<CommentView> viewcommentfuser(Integer uid);//看当前用户的评论
    ArrayList<CommentView> viewcommentfuar(Integer uid,Integer rid);//看用户在当前文物的评论

}
