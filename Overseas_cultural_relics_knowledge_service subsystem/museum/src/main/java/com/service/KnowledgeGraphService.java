package com.service;

import com.entity.GraphResponse;

public interface KnowledgeGraphService {
    GraphResponse getGraphData(Integer limit);
}