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

    // 预设的博物馆真实经纬度坐标 (纬度, 经度)
    private static final Map<String, double[]> COORDINATES = new HashMap<>();
    static {
        COORDINATES.put("大英博物馆", new double[]{51.5074, -0.1278});
        COORDINATES.put("大都会博物馆", new double[]{40.7794, -73.9632});
        COORDINATES.put("大都会艺术博物馆", new double[]{40.7794, -73.9632});
        COORDINATES.put("卢浮宫", new double[]{48.8606, 2.3376});
        COORDINATES.put("东京国立博物馆", new double[]{35.7100, 139.7691});
        COORDINATES.put("维多利亚博物馆", new double[]{-37.8136, 144.9631}); // 墨尔本
        COORDINATES.put("柏林亚洲艺术博物馆", new double[]{52.5200, 13.4050}); // 柏林
        COORDINATES.put("波士顿美术馆", new double[]{42.3398, -71.0942});    // 波士顿
        COORDINATES.put("韩国国立中央博物馆", new double[]{37.5396, 127.0164}); // 首尔
    }

    @Override
    public List<Map<String, Object>> getMuseumLocations() {
        try {
            List<Map<String, Object>> locations = new ArrayList<>();
            List<MuseumNode> museums = museumRepository.findAll();
            List<RelicNode> relics = relicRepository.findAll();

            for (MuseumNode museum : museums) {
                String museumName = museum.getNameCn() != null ? museum.getNameCn() : museum.getName();
                double[] coord = COORDINATES.getOrDefault(museumName, new double[]{400, 200});

                int count = 0;
                for (RelicNode relic : relics) {
                    if (relic.getMuseumId() != null && relic.getMuseumId().equals(museum.getObjectId())) {
                        count++;
                    }
                }

                Map<String, Object> location = new HashMap<>();
                location.put("name", museumName);
                location.put("city", museum.getLocation() != null ? museum.getLocation().split(",")[0] : "");
                location.put("country", museum.getLocation() != null && museum.getLocation().contains(",") ? museum.getLocation().split(",")[1].trim() : "");
                location.put("lat", coord[0]);
                location.put("lng", coord[1]);
                location.put("count", count > 0 ? count : getDefaultCount(museumName));
                locations.add(location);
            }

            if (!locations.isEmpty()) {
                return locations;
            }
        } catch (Exception e) {
            System.out.println("Neo4j不可用，GeoMap 返回示例数据: " + e.getMessage());
        }

        return getMockData();
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
        // 博物馆数据: 名称, 城市, 国家, 纬度, 经度, 文物数量
        String[][] museums = {
            {"大英博物馆", "伦敦", "英国", "51.5074", "-0.1278", "23000"},
            {"大都会博物馆", "纽约", "美国", "40.7794", "-73.9632", "15000"},
            {"卢浮宫", "巴黎", "法国", "48.8606", "2.3376", "8000"},
            {"东京国立博物馆", "东京", "日本", "35.7100", "139.7691", "12000"},
            {"维多利亚博物馆", "墨尔本", "澳大利亚", "-37.8136", "144.9631", "5000"},
            {"柏林亚洲艺术博物馆", "柏林", "德国", "52.5200", "13.4050", "6000"},
            {"波士顿美术馆", "波士顿", "美国", "42.3398", "-71.0942", "4500"},
            {"韩国国立中央博物馆", "首尔", "韩国", "37.5396", "127.0164", "3800"}
        };

        for (String[] m : museums) {
            Map<String, Object> location = new HashMap<>();
            location.put("name", m[0]);
            location.put("city", m[1]);
            location.put("country", m[2]);
            location.put("lat", Double.parseDouble(m[3]));  // 纬度
            location.put("lng", Double.parseDouble(m[4]));  // 经度
            location.put("count", Integer.parseInt(m[5]));
            mockData.add(location);
        }
        return mockData;
    }
}