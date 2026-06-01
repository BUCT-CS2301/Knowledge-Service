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
        try {
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
                        relicData.put("objectId", relic.getObjectId());
                        relicData.put("id", relic.getObjectId() != null ? relic.getObjectId().hashCode() : null);
                        relicData.put("description", relic.getDescription() != null ? relic.getDescription() : "");
                        relicData.put("image", relic.getImageUrl() != null ? relic.getImageUrl() : "");
                        relicList.add(relicData);
                    }
                }
                periodData.put("relics", relicList);
                timelineData.add(periodData);
            }

            if (!timelineData.isEmpty()) {
                return timelineData;
            }
        } catch (Exception e) {
            System.out.println("Neo4j不可用，Timeline 返回示例数据: " + e.getMessage());
        }

        return getSampleTimelineData();
    }

    private List<Map<String, Object>> getSampleTimelineData() {
        List<Map<String, Object>> timelineData = new ArrayList<>();
        String[][] samples = {
                {"商代", "约公元前1600-公元前1046年", "商代青铜鼎", "青铜器", "大英博物馆"},
                {"唐代", "公元618-公元907年", "唐代三彩骆驼", "陶俑", "大都会艺术博物馆"},
                {"宋代", "公元960-公元1279年", "宋代青瓷碗", "瓷器", "卢浮宫"},
                {"明代", "公元1368-公元1644年", "明代青花瓷瓶", "瓷器", "大英博物馆"},
                {"清代", "公元1636-公元1912年", "清代珐琅彩碗", "瓷器", "东京国立博物馆"}
        };

        for (String[] sample : samples) {
            Map<String, Object> periodData = new HashMap<>();
            periodData.put("dynasty", sample[0]);
            periodData.put("year", sample[1]);
            periodData.put("description", sample[0] + "时期代表性海外藏中国文物");

            Map<String, Object> relicData = new HashMap<>();
            relicData.put("name", sample[2]);
            relicData.put("type", sample[3]);
            relicData.put("museum", sample[4]);
            relicData.put("image", "https://example.com/default.jpg");

            periodData.put("relics", List.of(relicData));
            timelineData.add(periodData);
        }
        return timelineData;
    }
}
