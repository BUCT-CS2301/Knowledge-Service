package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("Artifact")
public class RelicNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Property("object_id")
    private String objectId;
    private String title;
    private String period;
    private String type;
    private String material;
    private String description;
    private String museumId;
    private String imageUrl;
}
