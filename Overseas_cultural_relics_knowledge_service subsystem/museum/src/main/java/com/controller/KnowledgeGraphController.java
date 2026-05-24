package com.controller;

import com.entity.GraphResponse;
import com.service.KnowledgeGraphService;
import com.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/data/knowledge-graph")
public class KnowledgeGraphController extends BaseController {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @GetMapping
    public JsonResult getGraphData(@RequestParam(required = false) Integer limit) {
        GraphResponse graphData = knowledgeGraphService.getGraphData(limit);
        return JsonResult.success(graphData);
    }
}