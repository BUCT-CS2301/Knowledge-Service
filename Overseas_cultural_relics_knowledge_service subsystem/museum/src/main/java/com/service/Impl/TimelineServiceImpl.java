package com.service.Impl;

import com.entity.PeriodNode;
import com.entity.RelicNode;
import com.repository.PeriodRepository;
import com.repository.RelicRepository;
import com.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private RelicRepository relicRepository;

    @Override
    public List<Map<String, Object>> getTimelineData() {
        List<Map<String, Object>> timelineData = new ArrayList<>();
        
        List<PeriodNode> periods = periodRepository.findAll();
        List<RelicNode> relics = relicRepository.findAll();

        for (PeriodNode period : periods) {
            Map<String, Object> periodData = new HashMap<>();
            periodData.put("dynasty", period.getName());
            periodData.put("year", (period.getStartYear() != null ? period.getStartYear() : "") + 
                    (period.getEndYear() != null ? " - " + period.getEndYear() : ""));
            periodData.put("description", period.getDescription() != null ? period.getDescription() : "暂无描述");

            List<Map<String, Object>> relicList = new ArrayList<>();
            for (RelicNode relic : relics) {
                if (relic.getPeriod() != null && relic.getPeriod().contains(period.getName())) {
                    Map<String, Object> relicData = new HashMap<>();
                    relicData.put("name", relic.getTitle() != null ? relic.getTitle() : "未知文物");
                    relicData.put("type", relic.getType() != null ? relic.getType() : "");
                    relicData.put("museum", relic.getMuseumId() != null ? relic.getMuseumId() : "");
                    relicData.put("image", relic.getImageUrl() != null ? relic.getImageUrl() : 
                            "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20relic&image_size=square");
                    relicList.add(relicData);
                }
            }
            periodData.put("relics", relicList);
            timelineData.add(periodData);
        }

        return timelineData;
    }
}