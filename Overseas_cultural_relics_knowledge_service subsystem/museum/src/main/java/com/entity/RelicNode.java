package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "Relic")
public class RelicNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String objectId;

    @Property
    private String title;

    @Property
    private String period;

    @Property
    private String type;

    @Property
    private String material;

    @Property
    private String description;

    @Property
    private String museumId;

    @Property
    private String imageUrl;
}