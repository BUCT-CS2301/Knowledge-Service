package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphNode {
    private String id;
    private String label;
    private String type;
    private String description;
    private String imageUrl;
    /** Neo4j 文物 UUID，仅 type=文物 时有值 */
    private String objectId;
}