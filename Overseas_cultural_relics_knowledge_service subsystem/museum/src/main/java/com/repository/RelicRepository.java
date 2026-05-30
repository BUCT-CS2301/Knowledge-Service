package com.repository;

import com.entity.RelicNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelicRepository extends Neo4jRepository<RelicNode, Long> {
    @Query("MATCH (n:Artifact) RETURN n LIMIT $limit")
    List<RelicNode> findRelicsWithLimit(Integer limit);

    @Query("MATCH (n:Artifact) WHERE n.title CONTAINS $keyword RETURN n")
    List<RelicNode> searchRelicsByKeyword(String keyword);
}