//package com.repository;
//
//import com.entity.Product_neo4j;
//import org.springframework.data.neo4j.repository.Neo4jRepository;
//import org.springframework.data.neo4j.repository.query.Query;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Component
//public interface ProductRepository extends Neo4jRepository<Product_neo4j,Long> {
//    @Query("MATCH (n:Relic) RETURN n ")
//    List<Product_neo4j> getUserNodeList();
//}
