package com.mapper;
import com.entity.Comment;
import com.entity.CommentView;
import com.service.ICommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentMapperTests {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ICommentService icommentservice;
    @Test
    public void insert(){
        Comment comment = new Comment();
        comment.setUid(1);
        comment.setRid(10);
        comment.setContent("6666ya");
        comment.setUser_comment(0);
        commentMapper.insert(comment);
    }
    @Test
    public void findByUidandRid(){
        List<CommentView> result;
        result=commentMapper.findByUidandRid(1,1);
        for(int i=0;i<result.size();i++)
            System.out.println(result.get(i).getUsername()+"  "+result.get(i).getRelicname()+" "+result.get(i).getContent()+" "+result.get(i).getCreated_time());
    }
    @Test
    public void find(){
        List<CommentView> result;
        result=commentMapper.findByRid(1);
        for(int i=0;i<result.size();i++)
            System.out.println(result.get(i).getCid());
        result=commentMapper.findByUid(1);
        for(int i=0;i<result.size();i++)
            System.out.println(result.get(i).getCid());
    }
    @Test
    public void find_by_cid(){
        Comment comment;
        comment = commentMapper.findBycid(1);
        System.out.println(comment.toString()+ comment.getCreatedTime() + comment.getModifiedTime());

    }
    @Test
    public void view(){
        ArrayList<CommentView> r1=icommentservice.viewcommentfrelics(1);
        ArrayList<CommentView> r2 =icommentservice.viewcommentfuar(1,1);
        ArrayList<CommentView> r3=icommentservice.viewcommentfuser(1);
        for(int i=0;i<r1.size();i++){
            System.out.println(r1.get(i).getContent());
        }
        for(int i=0;i<r2.size();i++){
            System.out.println(r2.get(i).getContent());
        }
        for(int i=0;i<r3.size();i++){
            System.out.println(r3.get(i).getContent());
        }
    }
    @Test
    public void delete(){
        Integer row = 0;
        row = icommentservice.deletecomment(4);
        System.out.println(row);
    }
}
