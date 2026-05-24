package com.service.Impl;

import com.entity.MuseumNode;
import com.entity.RelicNode;
import com.repository.MuseumRepository;
import com.repository.RelicRepository;
import com.service.GeoMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeoMapServiceImpl implements GeoMapService {

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private RelicRepository relicRepository;

    // 预设的博物馆地理坐标
    private static final Map<String, double[]> COORDINATES = new HashMap<>();
    static {
        COORDINATES.put("大英博物馆", new double[]{320, 110});
        COORDINATES.put("大都会博物馆", new double[]{680, 100});
        COORDINATES.put("卢浮宫", new double[]{300, 100});
        COORDINATES.put("东京国立博物馆", new double[]{720, 280});
        COORDINATES.put("维多利亚博物馆", new double[]{750, 330});
        COORDINATES.put("柏林亚洲艺术博物馆", new double[]{360, 90});
        COORDINATES.put("波士顿美术馆", new double[]{670, 95});
        COORDINATES.put("韩国国立中央博物馆", new double[]{690, 260});
    }

    @Override
    public List<Map<String, Object>> getMuseumLocations() {
        List<Map<String, Object>> locations = new ArrayList<>();
        List<MuseumNode> museums = museumRepository.findAll();
        List<RelicNode> relics = relicRepository.findAll();

        for (MuseumNode museum : museums) {
            String museumName = museum.getNameCn() != null ? museum.getNameCn() : museum.getName();
            double[] coord = COORDINATES.getOrDefault(museumName, new double[]{400, 200});
            
            // 统计该博物馆的文物数量
            int count = 0;
            for (RelicNode relic : relics) {
                if (relic.getMuseumId() != null && relic.getMuseumId().equals(museum.getObjectId())) {
                    count++;
                }
            }

            Map<String, Object> location = new HashMap<>();
            location.put("name", museumName);
            location.put("city", museum.getLocation() != null ? museum.getLocation().split(",")[0] : "");
            location.put("country", museum.getLocation() != null ? museum.getLocation().split(",")[1] : "");
            location.put("x", coord[0]);
            location.put("y", coord[1]);
            location.put("count", count > 0 ? count : getDefaultCount(museumName));
            
            locations.add(location);
        }

        // 如果数据库中没有博物馆数据，使用模拟数据
        if (locations.isEmpty()) {
            locations = getMockData();
        }

        return locations;
    }

    private int getDefaultCount(String museumName) {
        Map<String, Integer> defaultCounts = new HashMap<>();
        defaultCounts.put("大英博物馆", 23000);
        defaultCounts.put("大都会博物馆", 15000);
        defaultCounts.put("卢浮宫", 8000);
        defaultCounts.put("东京国立博物馆", 12000);
        defaultCounts.put("维多利亚博物馆", 5000);
        defaultCounts.put("柏林亚洲艺术博物馆", 6000);
        defaultCounts.put("波士顿美术馆", 4500);
        defaultCounts.put("韩国国立中央博物馆", 3800);
        return defaultCounts.getOrDefault(museumName, 1000);
    }

    private List<Map<String, Object>> getMockData() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        String[][] museums = {
            {"大英博物馆", "伦敦", "英国", "320", "110", "23000"},
            {"大都会博物馆", "纽约", "美国", "680", "100", "15000"},
            {"卢浮宫", "巴黎", "法国", "300", "100", "8000"},
            {"东京国立博物馆", "东京", "日本", "720", "280", "12000"},
            {"维多利亚博物馆", "墨尔本", "澳大利亚", "750", "330", "5000"},
            {"柏林亚洲艺术博物馆", "柏林", "德国", "360", "90", "6000"},
            {"波士顿美术馆", "波士顿", "美国", "670", "95", "4500"},
            {"韩国国立中央博物馆", "首尔", "韩国", "690", "260", "3800"}
        };

        for (String[] m : museums) {
            Map<String, Object> location = new HashMap<>();
            location.put("name", m[0]);
            location.put("city", m[1]);
            location.put("country", m[2]);
            location.put("x", Double.parseDouble(m[3]));
            location.put("y", Double.parseDouble(m[4]));
            location.put("count", Integer.parseInt(m[5]));
            mockData.add(location);
        }
        return mockData;
    }
}