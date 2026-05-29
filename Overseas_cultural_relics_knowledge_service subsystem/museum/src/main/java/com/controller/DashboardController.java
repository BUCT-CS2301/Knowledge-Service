package com.controller;

import com.service.DashboardService;
import com.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/data/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public JsonResult getDashboardData() {
        Map<String, Object> data = dashboardService.getDashboardData();
        return JsonResult.success(data);
    }
}