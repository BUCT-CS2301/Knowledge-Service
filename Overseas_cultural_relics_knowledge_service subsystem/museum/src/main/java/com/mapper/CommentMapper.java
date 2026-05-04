package com.mapper;
import com.entity.Comment;
import com.entity.CommentView;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Mapper
public interface CommentMapper {
    //插入数据 返回受影响的行数
    Integer insert(Comment comment);
    //删除数据 返回受影响的行数
    Integer delete(Integer cid);
    Comment findBycid(Integer cid);
    //返回查找到的cid的评论数据 无则返回null
    ArrayList<CommentView> findByUidandRid(Integer uid,Integer rid);
    ArrayList<CommentView> findByUid(Integer uid);
    ArrayList<CommentView> findByRid(Integer rid);
}
