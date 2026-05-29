package com.controller;

import com.service.TimelineService;
import com.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data/timeline")
public class TimelineController extends BaseController {

    @Autowired
    private TimelineService timelineService;

    @GetMapping
    public JsonResult getTimelineData() {
        List<Map<String, Object>> timelineData = timelineService.getTimelineData();
        return JsonResult.success(timelineData);
    }
}