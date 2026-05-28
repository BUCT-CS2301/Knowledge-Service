package com.service;

import com.entity.Cart;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class Neo4jArtifactSearchService {

    private static final int DEFAULT_LIMIT = 100;

    private static final String OPTIONAL_RELS = """
            OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)
            OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)
            OPTIONAL MATCH (a)-[:制作材质]->(mat:Material)
            OPTIONAL MATCH (a)-[:文物品类]->(t:ArtifactType)
            OPTIONAL MATCH (a)-[:展示图片]->(img:Image)
            """;

    private static final String OPTIONAL_EXCEPT_MUSEUM = """
            OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)
            OPTIONAL MATCH (a)-[:制作材质]->(mat:Material)
            OPTIONAL MATCH (a)-[:文物品类]->(t:ArtifactType)
            OPTIONAL MATCH (a)-[:展示图片]->(img:Image)
            """;

    private static final String OPTIONAL_EXCEPT_PERIOD = """
            OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)
            OPTIONAL MATCH (a)-[:制作材质]->(mat:Material)
            OPTIONAL MATCH (a)-[:文物品类]->(t:ArtifactType)
            OPTIONAL MATCH (a)-[:展示图片]->(img:Image)
            """;

    private static final String OPTIONAL_EXCEPT_MATERIAL = """
            OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)
            OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)
            OPTIONAL MATCH (a)-[:文物品类]->(t:ArtifactType)
            OPTIONAL MATCH (a)-[:展示图片]->(img:Image)
            """;

    private static final String OPTIONAL_EXCEPT_TYPE = """
            OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)
            OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)
            OPTIONAL MATCH (a)-[:制作材质]->(mat:Material)
            OPTIONAL MATCH (a)-[:展示图片]->(img:Image)
            """;

    /** 聚合可选关系，避免多图/多材质导致结果行重复 */
    private static final String AGG_AND_RETURN = """
            WITH a,
                 head([v IN collect(DISTINCT mat.name) WHERE v IS NOT NULL AND trim(v) <> '' | v]) AS material,
                 head([v IN collect(DISTINCT p.name) WHERE v IS NOT NULL AND trim(v) <> '' | v]) AS period,
                 head([v IN collect(DISTINCT t.name) WHERE v IS NOT NULL AND trim(v) <> '' | v]) AS artifactType,
                 head([v IN collect(DISTINCT m.name) WHERE v IS NOT NULL AND trim(v) <> '' | v]) AS museum,
                 head([u IN collect(DISTINCT img.url) WHERE u IS NOT NULL AND u <> 'unknown' AND trim(u) <> '' | u]) AS imageUrl
            RETURN a.object_id AS objectId,
                   a.title AS title,
                   coalesce(material, '') AS material,
                   coalesce(period, '') AS period,
                   coalesce(artifactType, '') AS type,
                   coalesce(museum, '') AS museum,
                   coalesce(imageUrl, '') AS imageUrl
            """;

    private final Neo4jClient neo4jClient;

    public Neo4jArtifactSearchService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<Cart> searchByKeyword(String keyword) {
        String kw = trim(keyword);
        if (kw.isEmpty()) {
            return Collections.emptyList();
        }
        String cypher = """
                MATCH (a:Artifact)
                """ + OPTIONAL_RELS + """
                WHERE a.title CONTAINS $kw
                   OR a.description CONTAINS $kw
                   OR coalesce(p.name, '') CONTAINS $kw
                   OR coalesce(m.name, '') CONTAINS $kw
                   OR coalesce(m.name_en, '') CONTAINS $kw
                   OR coalesce(mat.name, '') CONTAINS $kw
                   OR coalesce(t.name, '') CONTAINS $kw
                """ + AGG_AND_RETURN + " LIMIT $limit";
        return query(cypher, Map.of("kw", kw, "limit", DEFAULT_LIMIT));
    }

    public List<Cart> searchByMuseum(String museum) {
        String kw = trim(museum);
        if (kw.isEmpty()) {
            return Collections.emptyList();
        }
        String cypher = """
                MATCH (a:Artifact)-[:收藏馆藏]->(m:Museum)
                """ + OPTIONAL_EXCEPT_MUSEUM + """
                WHERE m.name CONTAINS $kw OR coalesce(m.name_en, '') CONTAINS $kw
                """ + AGG_AND_RETURN + " LIMIT $limit";
        return query(cypher, Map.of("kw", kw, "limit", DEFAULT_LIMIT));
    }

    public List<Cart> searchByMaterial(String material) {
        return searchByRelatedNode("制作材质", "Material", "mat", OPTIONAL_EXCEPT_MATERIAL, material);
    }

    public List<Cart> searchByPeriod(String period) {
        return searchByRelatedNode("所属朝代", "Period", "p", OPTIONAL_EXCEPT_PERIOD, period);
    }

    public List<Cart> searchByType(String type) {
        return searchByRelatedNode("文物品类", "ArtifactType", "t", OPTIONAL_EXCEPT_TYPE, type);
    }

    public List<Cart> searchByMulti(String museum, String material, String period, String type) {
        StringBuilder cypher = new StringBuilder("MATCH (a:Artifact)");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (isPresent(museum)) {
            cypher.append(" MATCH (a)-[:收藏馆藏]->(m:Museum)");
            conditions.add("(m.name CONTAINS $museum OR coalesce(m.name_en, '') CONTAINS $museum)");
            params.put("museum", trim(museum));
        }
        if (isPresent(material)) {
            cypher.append(" MATCH (a)-[:制作材质]->(mat:Material)");
            conditions.add("mat.name CONTAINS $material");
            params.put("material", trim(material));
        }
        if (isPresent(period)) {
            cypher.append(" MATCH (a)-[:所属朝代]->(p:Period)");
            conditions.add("p.name CONTAINS $period");
            params.put("period", trim(period));
        }
        if (isPresent(type)) {
            cypher.append(" MATCH (a)-[:文物品类]->(t:ArtifactType)");
            conditions.add("t.name CONTAINS $type");
            params.put("type", trim(type));
        }
        if (conditions.isEmpty()) {
            return Collections.emptyList();
        }
        if (!isPresent(museum)) {
            cypher.append(" OPTIONAL MATCH (a)-[:收藏馆藏]->(m:Museum)");
        }
        if (!isPresent(period)) {
            cypher.append(" OPTIONAL MATCH (a)-[:所属朝代]->(p:Period)");
        }
        if (!isPresent(material)) {
            cypher.append(" OPTIONAL MATCH (a)-[:制作材质]->(mat:Material)");
        }
        if (!isPresent(type)) {
            cypher.append(" OPTIONAL MATCH (a)-[:文物品类]->(t:ArtifactType)");
        }
        cypher.append(" OPTIONAL MATCH (a)-[:展示图片]->(img:Image)");
        cypher.append(" WHERE ").append(String.join(" AND ", conditions));
        cypher.append(AGG_AND_RETURN).append(" LIMIT $limit");
        params.put("limit", DEFAULT_LIMIT);
        return query(cypher.toString(), params);
    }

    public List<Cart> sortByName(boolean ascending) {
        String cypher = """
                MATCH (a:Artifact)
                """ + OPTIONAL_RELS + AGG_AND_RETURN
                + " ORDER BY a.title " + (ascending ? "ASC" : "DESC")
                + " LIMIT $limit";
        return query(cypher, Map.of("limit", DEFAULT_LIMIT));
    }

    public List<Cart> sortByPeriod(boolean ascending) {
        String cypher = """
                MATCH (a:Artifact)
                """ + OPTIONAL_RELS + AGG_AND_RETURN
                + " ORDER BY coalesce(p.name, '') " + (ascending ? "ASC" : "DESC")
                + " LIMIT $limit";
        return query(cypher, Map.of("limit", DEFAULT_LIMIT));
    }

    private List<Cart> searchByRelatedNode(String relType, String label, String nodeVar,
                                           String optionalBlock, String keyword) {
        String kw = trim(keyword);
        if (kw.isEmpty()) {
            return Collections.emptyList();
        }
        // 避免 text block 与 + 拼接产生 "WHEREmat" 等 Cypher 语法错误
        String cypher = "MATCH (a:Artifact)-[:" + relType + "]->(" + nodeVar + ":" + label + ")\n"
                + optionalBlock
                + "\nWHERE " + nodeVar + ".name CONTAINS $kw\n"
                + AGG_AND_RETURN + " LIMIT $limit";
        return query(cypher, Map.of("kw", kw, "limit", DEFAULT_LIMIT));
    }

    private List<Cart> query(String cypher, Map<String, Object> params) {
        Map<String, Cart> deduped = new LinkedHashMap<>();
        neo4jClient.query(cypher)
                .bindAll(params)
                .fetch()
                .all()
                .stream()
                .map(this::toCart)
                .filter(Objects::nonNull)
                .forEach(cart -> {
                    String key = cart.getObjectId() != null && !cart.getObjectId().isBlank()
                            ? cart.getObjectId()
                            : String.valueOf(cart.getId());
                    deduped.putIfAbsent(key, cart);
                });
        return new ArrayList<>(deduped.values());
    }

    private Cart toCart(Map<String, Object> row) {
        String objectId = stringVal(row.get("objectId"));
        String title = stringVal(row.get("title"));
        if (objectId.isEmpty() && title.isEmpty()) {
            return null;
        }
        Cart cart = new Cart();
        cart.setObjectId(objectId);
        cart.setId(toNumericId(objectId));
        cart.setObject_name(title);
        cart.setCat1(stringVal(row.get("material")));
        cart.setCat2(stringVal(row.get("period")));
        cart.setCat3(stringVal(row.get("type")));
        cart.setMakers_name(stringVal(row.get("museum")));
        cart.setImg_url(stringVal(row.get("imageUrl")));
        return cart;
    }

    private BigInteger toNumericId(String objectId) {
        return BigInteger.valueOf(Integer.toUnsignedLong(objectId.hashCode()));
    }

    private String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
