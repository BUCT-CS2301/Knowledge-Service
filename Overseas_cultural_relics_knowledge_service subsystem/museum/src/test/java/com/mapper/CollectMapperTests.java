package com.mapper;


import com.entity.Collect;
import com.entity.CollectView;
import com.service.CollectService;
import com.service.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CollectMapperTests {
    @Autowired
    private CollectMapper collectmapper;
    @Autowired
    private CollectService collectservice;
    @Test
    public void addcollection(){
        try
        {
            Collect collect = new Collect();
            collect.setRid(1);
            collect.setUid(1);
            collectservice.addcollection(collect);
        }
        catch(ServiceException e){
            System.out.println("收藏失败！"+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void delete(){
        try{
            collectservice.removecollection(1);
        }
        catch(ServiceException e){
            System.out.println("删除失败！"+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void view(){
        try{
            List<CollectView> result=collectservice.collectionfromuid(1);
            for(int i=0;i<result.size();i++){
                System.out.println(result.get(i).getId()+" "+result.get(i).getRid()+" "+result.get(i).getUid()+" "+result.get(i).getUsername()+" "+result.get(i).getRelicname());
            }
        }
        catch(ServiceException e){
            System.out.println("查找失败！"+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void test_mapper(){
        Collect collect;
        collect = collectmapper.findByuidandrid(1,1);
        System.out.println(collect.toString());

    }
    @Test
    public void findByUid_Rid(){
        Collect collect = collectservice.findByuidandrid(1,1);
        if(collect==null) System.out.println("未收藏");
        else System.out.println("已收藏");
    }
}
