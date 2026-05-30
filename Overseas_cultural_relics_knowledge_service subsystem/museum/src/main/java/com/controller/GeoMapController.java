package com.controller;

import com.service.GeoMapService;
import com.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data/geo-map")
public class GeoMapController extends BaseController {

    @Autowired
    private GeoMapService geoMapService;

    @GetMapping
    public JsonResult getMuseumLocations() {
        List<Map<String, Object>> locations = geoMapService.getMuseumLocations();
        return JsonResult.success(locations);
    }
}