package com.repository;

import com.entity.PeriodNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends Neo4jRepository<PeriodNode, Long> {
}