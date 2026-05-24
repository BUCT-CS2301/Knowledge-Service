package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = "BELONGS_TO")
public class RelicRelation {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private RelicNode relic;

    @EndNode
    private Object target;

    private String relationType;
}