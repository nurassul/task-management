package com.project.notificationservice.kafka;

import com.project.notificationservice.api.UserClient;
import com.project.notificationservice.api.service.EmailSenderService;
import com.project.notificationservice.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskEvent;
import user.model.User;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final UserClient userClient;
    private final EmailSenderService emailSenderService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void handleStatusChange(TaskEvent event) {
        log.info("Received event: {}", event);

        if (event.assignedUserId() == null) {
            return;
        }

        try {
            User assignedUser = userClient.getUserById(event.assignedUserId());

            log.info("Preparing email for: {} ({})", assignedUser.getUsername(), assignedUser.getEmail());
            log.info("Task {} changed status to {}", event.taskId(), event.newStatus());

            emailSenderService.sendTaskNotification(
                    assignedUser.getEmail(),
                    event.taskId(),
                    event.newStatus().name()
            );

            notificationService.saveNotification(event);
        } catch (Exception e) {
            log.error("Failed to send notification for user {}", event.assignedUserId(), e);
        }
    }
}
