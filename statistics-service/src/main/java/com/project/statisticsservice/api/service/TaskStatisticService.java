package com.project.statisticsservice.api.service;

import com.project.statisticsservice.api.dto.TaskStatsDto;
import task.kafka.TaskEvent;

public interface TaskStatisticService {


    void processEvent(TaskEvent event);
    TaskStatsDto getAllTaskStatistic();
}
