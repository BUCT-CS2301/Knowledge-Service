package com.mapper;
import com.entity.Collect;
import com.entity.CollectView;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@Mapper
public interface CollectMapper {
    //添加收藏
    Integer insert(Collect collect);
    //删除收藏
    Integer delete(Integer id);
    //id找信息
    Collect findByid(Integer id);
    //查找用户对文物收藏记录
    Collect findByuidandrid(Integer uid,Integer rid);
    //显示查找到的收藏
    ArrayList<CollectView> findByuid(Integer uid);
}
