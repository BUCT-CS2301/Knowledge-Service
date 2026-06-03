package com.controller;

import com.entity.*;
import com.service.ArtifactImageFallbackService;
import com.service.CollectService;
import com.service.ICartService;
import com.service.ICommentService;
import com.service.IProductService;
import com.service.Impl.ProductServiceImpl;
import com.service.exception.CollectduplicateException;
import com.service.exception.CommentPermissionException;
import com.service.exception.InsertException;
import com.service.exception.ProductNotFoundException;
import com.util.JsonResult;
import com.util.RelicKeyUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")//输入查询
public class ProductController extends BaseController{
    @Autowired
    private IProductService iProductService;
    @Autowired
    private ICartService iCartService;
    @Autowired
    private ICommentService iCommentService;
    @Autowired
    private CollectService collectService;
    @Autowired
    private com.service.Neo4jArtifactSearchService neo4jArtifactSearchService;
    @Autowired
    private ArtifactImageFallbackService artifactImageFallbackService;

    @RequestMapping("/detailByObjectId")//Neo4j 文物详情（按 objectId）
    public JsonResult<Map<String, Object>> findByObjectId(@RequestBody Map map){
        JsonResult<Map<String, Object>> result = new JsonResult<>();
        Object oid = map.get("objectId");
        Map<String, Object> detail = neo4jArtifactSearchService
                .findDetailByObjectId(oid == null ? null : String.valueOf(oid));
        if (detail == null) {
            result.setState(6000);
            result.setMessage("抱歉，您查询的文物不存在！");
            return result;
        }
        artifactImageFallbackService.fillMissingImage(detail);
        Object uidObj = map.get("uid");
        String oidStr = String.valueOf(detail.get("objectId"));
        int relicKey = RelicKeyUtil.toRelicKey(oidStr);
        detail.put("commentView", iCommentService.viewcommentfrelics(relicKey));
        if (uidObj != null) {
            try {
                int userId = Integer.parseInt(String.valueOf(uidObj));
                Collect existing = collectService.findByuidandrid(userId, relicKey);
                if (existing == null) {
                    existing = collectService.findByUidAndObjectId(userId, oidStr);
                }
                detail.put("if_collect", existing != null ? 1 : 0);
            } catch (NumberFormatException ignored) {
                detail.put("if_collect", 0);
            }
        } else {
            detail.put("if_collect", 0);
        }
        result.setData(detail);
        result.setState(200);
        result.setMessage("找到以下内容：");
        return result;
    }

    @RequestMapping("/searchById")//文物详情页面
    public JsonResult<ProductView> findById(@RequestBody Map map){
        JsonResult<ProductView> result = new JsonResult<ProductView>();
        try{
            String relicid = (String) map.get("rid");
            String userid = (String) map.get("uid");
            int relic_id = Integer.parseInt(relicid);
            Product p = new Product();
            p = iProductService.findByProduct_id(BigInteger.valueOf(relic_id));
            ProductView pp = new ProductView();
            BeanUtils.copyProperties(p, pp);
            pp.setCommentView(iCommentService.viewcommentfrelics(relic_id));
            List<Cart> recommend=new ArrayList<Cart>();
            recommend=iCartService.SearchCommentView(p.getMuseum(),p.getCat2(),p.getMakers_name(),p.getGeography(),p.getId());
            pp.setRecommend(recommend);
            if(userid!=null){
                int user_id = Integer.parseInt(userid);
                Collect collect = collectService.findByuidandrid(user_id,relic_id);
                if(collect!=null)
                    pp.setIf_collect(1);
                else  pp.setIf_collect(0);
            }
            result.setData(pp);
            result.setState(200);
            result.setMessage("找到以下内容：");
        }catch (ProductNotFoundException e){
            result.setState(6000);
            result.setMessage("抱歉，您查询的文物不存在！");
        }
        return result;
    }
    @RequestMapping("/searchById/comment")//评论文物
    public JsonResult<Integer> comment(@RequestBody Map map ){
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            String content = (String) map.get("content");
            String objectId = map.get("objectId") == null ? null : String.valueOf(map.get("objectId")).trim();
            String relicName = map.get("relicName") == null ? null : String.valueOf(map.get("relicName")).trim();
            int user_id = Integer.parseInt(userid);
            int relic_id;
            if (objectId != null && !objectId.isEmpty()) {
                relic_id = RelicKeyUtil.toRelicKey(objectId);
            } else {
                String relicid = (String) map.get("rid");
                relic_id = Integer.parseInt(relicid);
            }
            result.setData(iCommentService.notecomment(user_id, relic_id, content, objectId, relicName));
            result.setState(200);
            result.setMessage("评论成功");
        }catch(CommentPermissionException e){
            result.setMessage("您已被限制评论");
            result.setState(3000);
        }catch(InsertException e)
        {
            result.setMessage("评论出现未知错误");
            result.setState(6000);
        }
        return result;
    }
    @RequestMapping("/searchById/collect")//收藏文物
    public JsonResult<Integer> collect(@RequestBody Map map ){
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            int user_id = Integer.parseInt(userid);
            String objectId = map.get("objectId") == null ? null : String.valueOf(map.get("objectId")).trim();
            String relicName = map.get("relicName") == null ? null : String.valueOf(map.get("relicName")).trim();
            int relic_id;
            if (objectId != null && !objectId.isEmpty()) {
                relic_id = RelicKeyUtil.toRelicKey(objectId);
            } else {
                String relicid = (String) map.get("rid");
                relic_id = Integer.parseInt(relicid);
            }
            Collect collect = new Collect();
            collect.setRid(relic_id);
            collect.setUid(user_id);
            collect.setRelicObjectId(objectId);
            collect.setRelicName(relicName);
            result.setData(collectService.addcollection(collect));
            result.setState(200);
            result.setMessage("收藏成功");
        } catch(CollectduplicateException e){
            result.setMessage("请勿重复收藏！");
            result.setState(5000);
        }
        return result;
    }

    /** 文物下的全部用户评论（社区列表，不依赖 MySQL 文物表是否存在） */
    @RequestMapping("/relicComments")
    public JsonResult<ArrayList<CommentView>> relicComments(@RequestBody Map map) {
        JsonResult<ArrayList<CommentView>> result = new JsonResult<>();
        try {
            String objectId = map.get("objectId") == null ? null : String.valueOf(map.get("objectId")).trim();
            int relicId;
            if (objectId != null && !objectId.isEmpty()) {
                relicId = RelicKeyUtil.toRelicKey(objectId);
            } else {
                relicId = Integer.parseInt(String.valueOf(map.get("rid")));
            }
            result.setData(iCommentService.viewcommentfrelics(relicId));
            result.setState(200);
            result.setMessage("评论列表");
        } catch (Exception e) {
            result.setState(4000);
            result.setMessage("评论加载失败");
            result.setData(new ArrayList<>());
        }
        return result;
    }


}
