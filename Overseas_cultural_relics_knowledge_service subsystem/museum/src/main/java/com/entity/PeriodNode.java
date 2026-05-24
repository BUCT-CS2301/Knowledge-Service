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
@NodeEntity(label = "Period")
public class PeriodNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @Property
    private String startYear;

    @Property
    private String endYear;

    @Property
    private String description;
}