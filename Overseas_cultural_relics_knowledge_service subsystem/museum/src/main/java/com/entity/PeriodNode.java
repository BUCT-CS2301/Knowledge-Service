package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("Period")
public class PeriodNode implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String startYear;
    private String endYear;
    private String description;
}
