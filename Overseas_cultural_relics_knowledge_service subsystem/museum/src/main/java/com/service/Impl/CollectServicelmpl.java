package com.service.Impl;

import com.entity.Collect;
import com.entity.CollectView;
import com.mapper.CollectMapper;
import com.service.CollectService;
import com.service.exception.CollectduplicateException;
import com.service.exception.DeleteException;
import com.service.exception.DeleteLoss;
import com.service.exception.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CollectServicelmpl implements CollectService {
    @Autowired
    private CollectMapper collectmapper;
    @Override
    public Integer addcollection(Collect collect){
        Collect result =collectmapper.findByuidandrid(collect.getUid(),collect.getRid());
        if(result!=null){
            throw new CollectduplicateException("请勿重复添加");
        }
        Integer rows=collectmapper.insert(collect);
//        if(rows!=1){
//            throw new InsertException("添加出现未知错误");
//        }
        return rows;
    }

    @Override
    public Integer removecollection(Integer id) {
        Collect result=collectmapper.findByid(id);
        if(result==null){
            throw new DeleteLoss("取消收藏信息未找到");
        }
        Integer rows=collectmapper.delete(id);
//        if(rows!=1){
//            throw new DeleteException("取消收藏未知错误");
//        }
        return rows;
    }

    @Override
    public List<CollectView> collectionfromuid(Integer uid) {
        return collectmapper.findByuid(uid);
    }

    @Override
    public Collect findByuidandrid(Integer uid, Integer rid) {
        Collect result=collectmapper.findByuidandrid(uid,rid);
//        if(result==null){
//            throw new DeleteLoss("收藏信息未找到");
//        }
        return result;
    }
}
