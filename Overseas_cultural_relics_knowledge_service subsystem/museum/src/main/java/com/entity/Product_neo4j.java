package com.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Product_neo4j implements Serializable {
    private Long id;
    private String name;
}
