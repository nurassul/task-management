package com.project.statisticsservice.kafka;

import com.project.statisticsservice.api.service.TaskStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticKafkaListener {

    private final TaskStatisticService statisticService;

    @KafkaListener(topics = "task-events", groupId = "statistic-group")
    public void handleTaskEvent(TaskEvent event) {
        log.info("Received event: {}", event);

        statisticService.processEvent(event);
    }
}
