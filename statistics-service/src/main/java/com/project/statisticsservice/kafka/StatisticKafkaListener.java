package com.project.statisticsservice.kafka;

import com.project.statisticsservice.api.service.taskapi.TaskStatisticService;
import com.project.statisticsservice.api.service.userapi.UserStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticKafkaListener {

    private final TaskStatisticService taskStatisticService;
    private final UserStatisticService userStatisticService;

    @KafkaListener(topics = "task-events", groupId = "statistic-group")
    public void handleTaskEvent(TaskEvent event) {
        log.info("Received event: {}", event);

        taskStatisticService.processEvent(event);
        userStatisticService.processUserStats(event);
    }
}
