package com.service;

import com.entity.Collect;
import com.entity.CollectView;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
public interface CollectService {
    //添加收藏
    Integer addcollection(Collect collect);
    //取消收藏
    Integer removecollection(Integer id);
    //列出用户收藏表
    List<CollectView> collectionfromuid(Integer uid);

    Collect findByuidandrid(Integer uid,Integer rid);
}
