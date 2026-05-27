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
@NodeEntity(label = "Museum")
public class MuseumNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String objectId;

    @Property
    private String name;

    @Property
    private String nameCn;

    @Property
    private String location;

    @Property
    private String website;
}