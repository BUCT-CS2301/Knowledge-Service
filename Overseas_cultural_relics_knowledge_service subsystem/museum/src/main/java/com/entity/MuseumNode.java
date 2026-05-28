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
@Node("Museum")
public class MuseumNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String objectId;
    private String name;
    @Property("name_en")
    private String nameCn;
    private String location;
    private String website;
}
