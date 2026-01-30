package com.project.notificationservice.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskStatusChangedEvent;

@Component
@Slf4j
public class NotificationKafkaListener {

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void handleStatusChange(TaskStatusChangedEvent event) {
        switch (event.newStatus()) {
            case IN_PROGRESS -> {
                log.info("👷‍♂️ User {} взялся за работу над задачей {}",
                        event.assignedUserId(), event.taskId());
            }
            case DONE -> {
                log.info("✅ User {} закончил задачу {}",
                        event.assignedUserId(), event.taskId());
            }
            default -> log.warn("Неизвестный статус: {}", event.newStatus());
        }
    }

}
