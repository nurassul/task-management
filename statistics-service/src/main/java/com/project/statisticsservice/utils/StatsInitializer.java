package com.project.statisticsservice.utils;


import com.project.statisticsservice.repository.TaskStatisticRepository;
import com.project.statisticsservice.repository.entity.TaskStatsEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StatsInitializer {

    private final TaskStatisticRepository repository;

    @PostConstruct
    public void init() {
        if(repository.count() == 0) {
            TaskStatsEntity taskStatsEntity = new TaskStatsEntity();
            taskStatsEntity.setId(1L);
            repository.save(taskStatsEntity);
        }
    }

}
