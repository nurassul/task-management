package com.project.notificationservice.kafka;


import com.project.notificationservice.api.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task.kafka.TaskStatusChangedEvent;
import user.model.User;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final UserClient userClient;

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void handleStatusChange(TaskStatusChangedEvent event) {

        log.info("Получено событие: {}", event);

        // 1. Узнаем, кому отправлять (Assigned User)
        if (event.assignedUserId() != null) {
            try {
                // Магический вызов HTTP-запроса через Feign
                User creatorUser = userClient.getUserById(event.creatorId());

                log.info("📧 Готовим письмо для: {} ({})", creatorUser.getUsername(), creatorUser.getEmail());
                log.info("Тема: Задача {} перешла в статус {}", event.taskId(), event.newStatus());

                // В следующем шаге тут будет вызов sendEmail(user.email(), ...)

            } catch (Exception e) {
                log.error("Не удалось получить данные пользователя {}: {}", event.assignedUserId(), e.getMessage());
            }
        }
    }

}
