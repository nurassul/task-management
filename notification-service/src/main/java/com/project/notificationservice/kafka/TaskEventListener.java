package com.project.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = {"task-created", "task-updated", "task-deleted"},
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTaskEvent(TaskEventDto task) {
        log.info("========================================");
        log.info("📬 Received task event!");
        log.info("Task ID: {}", task.getId());
        log.info("Task Status: {}", task.getTaskStatus());
        log.info("Creator ID: {}", task.getCreatorId());
        log.info("========================================");

        sendNotification(task);
    }

    private void sendNotification(TaskEventDto task) {
        log.info("📧 Sending notification to creator {}",
                task.getCreatorId());
    }
}
