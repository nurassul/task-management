package com.project.statisticsservice.api.service.userapi;

import com.project.statisticsservice.api.dto.user.UserStatsDto;
import task.kafka.TaskEvent;

public interface UserStatisticService {
    void processUserStats(TaskEvent event);

    UserStatsDto getUserStatsById(Long userId);
}
