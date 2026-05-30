package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
public class RelicRelation {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Object target;

    private String relationType;
}
