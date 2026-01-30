package com.project.notificationservice.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskCompletedDto;
import task.kafka.TaskInProgressDto;

@Component
@Slf4j
public class NotificationKafkaListener {

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void logTaskCompleted(TaskCompletedDto event) {
        log.info("🔔 [NOTIFICATION] Задача {} завершена! Создатель: {}, Исполнитель: {}",
                event.taskId(), event.creatorId(), event.assignedUserId());
    }

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void logTaskInProgress(TaskInProgressDto event) {
        log.info("🔔 [NOTIFICATION] Задача {} находится в прогрессе! Создатель: {}, Исполнитель: {}",
                event.taskId(), event.creatorId(), event.assignedUserId());
    }
}
