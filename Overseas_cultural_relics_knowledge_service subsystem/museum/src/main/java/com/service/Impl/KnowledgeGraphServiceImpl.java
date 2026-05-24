package com.service.Impl;

import com.entity.*;
import com.repository.MuseumRepository;
import com.repository.PeriodRepository;
import com.repository.RelicRepository;
import com.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    @Autowired
    private RelicRepository relicRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Override
    public GraphResponse getGraphData(Integer limit) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();

        List<RelicNode> relics = relicRepository.findRelicsWithLimit(limit != null ? limit : 20);
        List<MuseumNode> museums = museumRepository.findAll();
        List<PeriodNode> periods = periodRepository.findAll();

        for (RelicNode relic : relics) {
            nodes.add(new GraphNode(
                    "relic_" + relic.getId(),
                    relic.getTitle(),
                    "文物",
                    relic.getDescription() != null ? relic.getDescription() : "暂无描述",
                    relic.getImageUrl()
            ));
        }

        for (MuseumNode museum : museums) {
            nodes.add(new GraphNode(
                    "museum_" + museum.getId(),
                    museum.getNameCn() != null ? museum.getNameCn() : museum.getName(),
                    "博物馆",
                    museum.getLocation() != null ? museum.getLocation() : "暂无位置信息",
                    null
            ));
        }

        for (PeriodNode period : periods) {
            nodes.add(new GraphNode(
                    "period_" + period.getId(),
                    period.getName(),
                    "朝代",
                    period.getDescription() != null ? period.getDescription() : "暂无描述",
                    null
            ));
        }

        for (RelicNode relic : relics) {
            for (MuseumNode museum : museums) {
                if (relic.getMuseumId() != null && relic.getMuseumId().equals(museum.getObjectId())) {
                    links.add(new GraphLink("relic_" + relic.getId(), "museum_" + museum.getId(), "收藏于"));
                }
            }

            for (PeriodNode period : periods) {
                if (relic.getPeriod() != null && relic.getPeriod().contains(period.getName())) {
                    links.add(new GraphLink("relic_" + relic.getId(), "period_" + period.getId(), "属于"));
                }
            }
        }

        if (relics.size() >= 2) {
            for (int i = 0; i < relics.size() - 1; i++) {
                links.add(new GraphLink("relic_" + relics.get(i).getId(), "relic_" + relics.get(i + 1).getId(), "相关"));
            }
        }

        return new GraphResponse(nodes, links);
    }
}