package com.service.Impl;

import com.entity.*;
import com.repository.MuseumRepository;
import com.repository.PeriodRepository;
import com.repository.RelicRepository;
import com.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    @Autowired
    private RelicRepository relicRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Override
    public GraphResponse getGraphData(Integer limit) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();

        try {
            // 从Neo4j获取数据
            List<RelicNode> relics = relicRepository.findRelicsWithLimit(limit != null ? limit : 20);
            List<MuseumNode> museums = museumRepository.findAll();
            List<PeriodNode> periods = periodRepository.findAll();

            // 如果Neo4j没有数据，使用示例数据
            if (relics.isEmpty() && museums.isEmpty() && periods.isEmpty()) {
                return getSampleData();
            }

            for (RelicNode relic : relics) {
                nodes.add(new GraphNode(
                        "relic_" + relic.getId(),
                        relic.getTitle(),
                        "文物",
                        relic.getDescription() != null ? relic.getDescription() : "暂无描述",
                        relic.getImageUrl()
                ));
            }

            for (MuseumNode museum : museums) {
                nodes.add(new GraphNode(
                        "museum_" + museum.getId(),
                        museum.getNameCn() != null ? museum.getNameCn() : museum.getName(),
                        "博物馆",
                        museum.getLocation() != null ? museum.getLocation() : "暂无位置信息",
                        null
                ));
            }

            for (PeriodNode period : periods) {
                nodes.add(new GraphNode(
                        "period_" + period.getId(),
                        period.getName(),
                        "朝代",
                        period.getDescription() != null ? period.getDescription() : "暂无描述",
                        null
                ));
            }

            for (RelicNode relic : relics) {
                for (MuseumNode museum : museums) {
                    if (relic.getMuseumId() != null && museum.getObjectId() != null &&
                        relic.getMuseumId().equals(museum.getObjectId())) {
                        links.add(new GraphLink("relic_" + relic.getId(), "museum_" + museum.getId(), "收藏于"));
                    }
                }

                for (PeriodNode period : periods) {
                    if (relic.getPeriod() != null && period.getName() != null &&
                        relic.getPeriod().contains(period.getName())) {
                        links.add(new GraphLink("relic_" + relic.getId(), "period_" + period.getId(), "属于"));
                    }
                }
            }

            // 文物之间的"相关"关系
            if (relics.size() >= 2) {
                for (int i = 0; i < relics.size() - 1; i++) {
                    links.add(new GraphLink("relic_" + relics.get(i).getId(), "relic_" + relics.get(i + 1).getId(), "相关"));
                }
            }

            return new GraphResponse(nodes, links);
        } catch (Exception e) {
            // Neo4j连接失败时，返回示例数据
            System.out.println("Neo4j不可用，返回示例数据: " + e.getMessage());
            return getSampleData();
        }
    }

    // 返回完整的 Mock 数据
    private GraphResponse getSampleData() {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();

        // ===== 添加博物馆节点 =====
        nodes.add(new GraphNode("museum_1", "大英博物馆", "博物馆", "英国伦敦", null));
        nodes.add(new GraphNode("museum_2", "大都会艺术博物馆", "博物馆", "美国纽约", null));
        nodes.add(new GraphNode("museum_3", "卢浮宫", "博物馆", "法国巴黎", null));
        nodes.add(new GraphNode("museum_4", "故宫博物院", "博物馆", "中国北京", null));
        nodes.add(new GraphNode("museum_5", "艾尔米塔什博物馆", "博物馆", "俄罗斯圣彼得堡", null));
        nodes.add(new GraphNode("museum_6", "弗利尔美术馆", "博物馆", "美国华盛顿", null));

        // ===== 添加朝代节点 =====
        nodes.add(new GraphNode("period_1", "商代", "朝代", "约公元前1600-公元前1046年", null));
        nodes.add(new GraphNode("period_2", "西周", "朝代", "公元前1046-公元前771年", null));
        nodes.add(new GraphNode("period_3", "战国", "朝代", "公元前475-公元前221年", null));
        nodes.add(new GraphNode("period_4", "汉代", "朝代", "公元前206年-公元220年", null));
        nodes.add(new GraphNode("period_5", "唐代", "朝代", "公元618-公元907年", null));
        nodes.add(new GraphNode("period_6", "宋代", "朝代", "公元960-公元1279年", null));
        nodes.add(new GraphNode("period_7", "元代", "朝代", "公元1271-公元1368年", null));
        nodes.add(new GraphNode("period_8", "明代", "朝代", "公元1368-公元1644年", null));
        nodes.add(new GraphNode("period_9", "清代", "朝代", "公元1636-公元1912年", null));

        // ===== 添加文物节点 =====
        nodes.add(new GraphNode("relic_1", "商代青铜鼎", "文物", "商代晚期青铜祭祀用鼎，造型庄重，纹饰精美", null));
        nodes.add(new GraphNode("relic_2", "唐代三彩骆驼", "文物", "唐代巩义窑烧制的三彩骆驼，姿态生动，色彩艳丽", null));
        nodes.add(new GraphNode("relic_3", "宋代青瓷碗", "文物", "南宋龙泉窑青瓷碗，釉色青翠欲滴，胎质细腻", null));
        nodes.add(new GraphNode("relic_4", "明代青花瓷瓶", "文物", "明代永乐年间青花瓷瓶，绘制缠枝莲纹", null));
        nodes.add(new GraphNode("relic_5", "清代珐琅彩碗", "文物", "清代乾隆年间珐琅彩碗，彩绘花鸟纹饰", null));
        nodes.add(new GraphNode("relic_6", "战国玉璧", "文物", "战国时期玉璧，玉质温润，雕工精细", null));
        nodes.add(new GraphNode("relic_7", "东汉陶俑", "文物", "东汉时期彩绘陶俑，造型生动", null));
        nodes.add(new GraphNode("relic_8", "元青花梅瓶", "文物", "元代青花人物故事梅瓶，绘制人物故事图案", null));
        nodes.add(new GraphNode("relic_9", "西周青铜簋", "文物", "西周早期青铜簋，器身饰有兽面纹", null));
        nodes.add(new GraphNode("relic_10", "唐代银茶碾", "文物", "唐代宫廷御用银茶碾，造型精美", null));

        // ===== 文物-博物馆关系 (收藏于) =====
        links.add(new GraphLink("relic_1", "museum_1", "收藏于"));
        links.add(new GraphLink("relic_2", "museum_2", "收藏于"));
        links.add(new GraphLink("relic_3", "museum_3", "收藏于"));
        links.add(new GraphLink("relic_4", "museum_4", "收藏于"));
        links.add(new GraphLink("relic_5", "museum_5", "收藏于"));
        links.add(new GraphLink("relic_6", "museum_6", "收藏于"));
        links.add(new GraphLink("relic_7", "museum_1", "收藏于"));
        links.add(new GraphLink("relic_8", "museum_2", "收藏于"));
        links.add(new GraphLink("relic_9", "museum_3", "收藏于"));
        links.add(new GraphLink("relic_10", "museum_4", "收藏于"));

        // ===== 文物-朝代关系 (属于) =====
        links.add(new GraphLink("relic_1", "period_1", "属于"));   // 商代青铜鼎 -> 商代
        links.add(new GraphLink("relic_9", "period_2", "属于"));   // 西周青铜簋 -> 西周
        links.add(new GraphLink("relic_6", "period_3", "属于"));   // 战国玉璧 -> 战国
        links.add(new GraphLink("relic_7", "period_4", "属于"));   // 东汉陶俑 -> 汉代
        links.add(new GraphLink("relic_2", "period_5", "属于"));   // 唐三彩骆驼 -> 唐代
        links.add(new GraphLink("relic_10", "period_5", "属于"));  // 唐代银茶碾 -> 唐代
        links.add(new GraphLink("relic_3", "period_6", "属于"));   // 宋代青瓷碗 -> 宋代
        links.add(new GraphLink("relic_8", "period_7", "属于"));   // 元青花梅瓶 -> 元代
        links.add(new GraphLink("relic_4", "period_8", "属于"));   // 明代青花瓷瓶 -> 明代
        links.add(new GraphLink("relic_5", "period_9", "属于"));   // 清代珐琅彩碗 -> 清代

        // ===== 文物之间的相关关系 =====
        links.add(new GraphLink("relic_1", "relic_9", "相关"));   // 青铜鼎 - 青铜簋
        links.add(new GraphLink("relic_2", "relic_10", "相关"));  // 三彩骆驼 - 银茶碾
        links.add(new GraphLink("relic_3", "relic_4", "相关"));   // 青瓷碗 - 青花瓷瓶
        links.add(new GraphLink("relic_5", "relic_8", "相关"));   // 珐琅彩碗 - 元青花

        return new GraphResponse(nodes, links);
    }
}