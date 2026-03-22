package com.project.statisticsservice.api.service.taskapi;

import com.project.statisticsservice.api.dto.task.TaskStatsDto;
import task.kafka.TaskEvent;

public interface TaskStatisticService {

    void processEvent(TaskEvent event);
    TaskStatsDto getAllTaskStatistic();
}
