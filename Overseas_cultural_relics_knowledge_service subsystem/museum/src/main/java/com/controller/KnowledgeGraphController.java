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

    /** limit：演示子图文物数量，默认 25，最大 40 */
    @GetMapping
    public JsonResult getGraphData(@RequestParam(required = false, defaultValue = "25") Integer limit) {
        GraphResponse graphData = knowledgeGraphService.getGraphData(limit);
        return JsonResult.success(graphData);
    }
}