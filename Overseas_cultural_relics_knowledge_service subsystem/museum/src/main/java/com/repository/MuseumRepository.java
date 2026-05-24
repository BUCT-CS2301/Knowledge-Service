package com.repository;

import com.entity.MuseumNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuseumRepository extends Neo4jRepository<MuseumNode, Long> {
}