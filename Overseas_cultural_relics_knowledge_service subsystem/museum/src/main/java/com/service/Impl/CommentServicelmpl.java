package com.service.Impl;

import com.entity.Comment;
import com.entity.CommentView;
import com.mapper.CommentMapper;
import com.mapper.UserMapper;
import com.service.ICommentService;
import com.service.IUserService;
import com.service.exception.CommentPermissionException;
import com.service.exception.DeleteException;
import com.service.exception.DeleteLoss;
import com.service.exception.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
public class CommentServicelmpl implements ICommentService {
    @Autowired
    CommentMapper commentmapper;
    @Autowired
    UserMapper userMapper;
    @Override
    public Integer notecomment(Integer uid,Integer rid,String text){
        int user_comment = userMapper.findByUser_id(uid).getUser_comment();
        if(user_comment==3){
            throw new CommentPermissionException("用户无权限评论");
        }
        Comment comment=new Comment();
        comment.setUid(uid);
        comment.setRid(rid);
        comment.setContent(text);
        comment.setUser_comment(user_comment);
        Date now=new Date();
        comment.setModifiedTime(now);
        comment.setCreatedTime(now);
        Integer rows=commentmapper.insert(comment);
        if(rows!=1){
            throw new InsertException("评论出现未知错误");
        }
        return rows;
    }
    @Override
    public Integer deletecomment(Integer cid){
        Comment comment = commentmapper.findBycid(cid);
        if(comment==null){
            throw new DeleteLoss("未找到删除信息");
        }
        Integer rows=commentmapper.delete(cid);
        if(rows!=1){
            throw new DeleteException("删除出现未知错误");
        }
        return rows;
    }
    @Override
    public ArrayList<CommentView> viewcommentfrelics(Integer rid){
        return commentmapper.findByRid(rid);
    }

    @Override
    public ArrayList<CommentView> viewcommentfuser(Integer uid){
        return commentmapper.findByUid(uid);
    }

    @Override
    public ArrayList<CommentView> viewcommentfuar(Integer uid,Integer rid){
        return commentmapper.findByUidandRid(uid,rid);
    }
}
