package com.service.Impl;

import com.entity.GraphLink;
import com.entity.GraphNode;
import com.entity.GraphResponse;
import com.service.KnowledgeGraphService;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private static final int DEFAULT_DEMO_LIMIT = 25;
    private static final int MAX_DEMO_LIMIT = 40;

    private static final String DEMO_SUBGRAPH_CYPHER = """
            MATCH (a:Artifact)
            WHERE coalesce(a.title, '') <> ''
            WITH a
            ORDER BY coalesce(a.object_id, elementId(a))
            LIMIT $limit
            OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)
            OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)
            RETURN elementId(a) AS artifactId,
                   coalesce(a.title, '') AS title,
                   coalesce(a.description, '') AS description,
                   collect(DISTINCT m.name) AS museumNames,
                   collect(DISTINCT p.name) AS periodNames
            """;

    private final Neo4jClient neo4jClient;

    public KnowledgeGraphServiceImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public GraphResponse getGraphData(Integer limit) {
        int artifactLimit = normalizeLimit(limit);
        try {
            GraphResponse fromNeo4j = buildDemoSubgraph(artifactLimit);
            if (fromNeo4j != null && !fromNeo4j.getNodes().isEmpty()) {
                return fromNeo4j;
            }
        } catch (Exception e) {
            System.out.println("Neo4j 演示子图查询失败，返回内置示例: " + e.getMessage());
        }
        return getSampleData(artifactLimit);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_DEMO_LIMIT;
        }
        return Math.min(limit, MAX_DEMO_LIMIT);
    }

    private GraphResponse buildDemoSubgraph(int artifactLimit) {
        List<Map<String, Object>> rows = new ArrayList<>(neo4jClient.query(DEMO_SUBGRAPH_CYPHER)
                .bind(artifactLimit).to("limit")
                .fetch()
                .all());

        if (rows.isEmpty()) {
            return null;
        }

        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();
        Map<String, GraphNode> nodeIndex = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String artifactId = stringVal(row.get("artifactId"));
            if (artifactId.isEmpty()) {
                continue;
            }
            String relicNodeId = "relic_" + artifactId;
            addNode(nodeIndex, nodes, new GraphNode(
                    relicNodeId,
                    stringVal(row.get("title")),
                    "文物",
                    stringVal(row.get("description")),
                    null
            ));

            for (String museumName : stringList(row.get("museumNames"))) {
                if (museumName.isEmpty()) {
                    continue;
                }
                String museumNodeId = "museum_" + museumName;
                addNode(nodeIndex, nodes, new GraphNode(
                        museumNodeId,
                        museumName,
                        "博物馆",
                        "馆藏机构",
                        null
                ));
                links.add(new GraphLink(relicNodeId, museumNodeId, "收藏于"));
            }

            for (String periodName : stringList(row.get("periodNames"))) {
                if (periodName.isEmpty()) {
                    continue;
                }
                String periodNodeId = "period_" + periodName;
                addNode(nodeIndex, nodes, new GraphNode(
                        periodNodeId,
                        periodName,
                        "朝代",
                        "所属年代",
                        null
                ));
                links.add(new GraphLink(relicNodeId, periodNodeId, "属于"));
            }
        }

        return new GraphResponse(nodes, links);
    }

    private void addNode(Map<String, GraphNode> nodeIndex, List<GraphNode> nodes, GraphNode node) {
        if (!nodeIndex.containsKey(node.getId())) {
            nodeIndex.put(node.getId(), node);
            nodes.add(node);
        }
    }

    private String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    @SuppressWarnings("unchecked")
    private List<String> stringList(Object value) {
        Set<String> out = new HashSet<>();
        if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                String s = stringVal(item);
                if (!s.isEmpty()) {
                    out.add(s);
                }
            }
        } else {
            String s = stringVal(value);
            if (!s.isEmpty()) {
                out.add(s);
            }
        }
        return new ArrayList<>(out);
    }

    /** 内置示例（离线兜底，规模固定且可读） */
    private GraphResponse getSampleData(int artifactLimit) {
        return getSampleData();
    }

    private GraphResponse getSampleData() {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();

        nodes.add(new GraphNode("museum_1", "大英博物馆", "博物馆", "英国伦敦", null));
        nodes.add(new GraphNode("museum_2", "大都会艺术博物馆", "博物馆", "美国纽约", null));
        nodes.add(new GraphNode("museum_3", "卢浮宫", "博物馆", "法国巴黎", null));
        nodes.add(new GraphNode("museum_4", "故宫博物院", "博物馆", "中国北京", null));

        nodes.add(new GraphNode("period_1", "商代", "朝代", "约公元前1600-公元前1046年", null));
        nodes.add(new GraphNode("period_2", "唐代", "朝代", "公元618-公元907年", null));
        nodes.add(new GraphNode("period_3", "宋代", "朝代", "公元960-公元1279年", null));
        nodes.add(new GraphNode("period_4", "明代", "朝代", "公元1368-公元1644年", null));

        nodes.add(new GraphNode("relic_1", "商代青铜鼎", "文物", "商代晚期青铜祭祀用鼎", null));
        nodes.add(new GraphNode("relic_2", "唐代三彩骆驼", "文物", "唐代三彩骆驼俑", null));
        nodes.add(new GraphNode("relic_3", "宋代青瓷碗", "文物", "南宋龙泉窑青瓷碗", null));
        nodes.add(new GraphNode("relic_4", "明代青花瓷瓶", "文物", "明代青花瓷瓶", null));

        links.add(new GraphLink("relic_1", "museum_1", "收藏于"));
        links.add(new GraphLink("relic_2", "museum_2", "收藏于"));
        links.add(new GraphLink("relic_3", "museum_3", "收藏于"));
        links.add(new GraphLink("relic_4", "museum_4", "收藏于"));
        links.add(new GraphLink("relic_1", "period_1", "属于"));
        links.add(new GraphLink("relic_2", "period_2", "属于"));
        links.add(new GraphLink("relic_3", "period_3", "属于"));
        links.add(new GraphLink("relic_4", "period_4", "属于"));

        return new GraphResponse(nodes, links);
    }
}
