package com.project.notificationservice.kafka;


import com.project.notificationservice.api.UserClient;
import com.project.notificationservice.api.repository.NotificationRepository;
import com.project.notificationservice.api.service.EmailSenderService;
import com.project.notificationservice.api.service.NotificationService;
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
    private final EmailSenderService emailSenderService;
    private final NotificationService notificationService;


    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void handleStatusChange(TaskStatusChangedEvent event) {

        log.info("Получено событие: {}", event);

        if (event.assignedUserId() != null) {
            try {
                User creatorUser = userClient.getUserById(event.creatorId());

                log.info("📧 Готовим письмо для: {} ({})", creatorUser.getUsername(), creatorUser.getEmail());
                log.info("Тема: Задача {} перешла в статус {}", event.taskId(), event.newStatus());

                emailSenderService.sendTaskNotification(creatorUser.getEmail(), creatorUser.getId(), event.newStatus().name());
                notificationService.saveNotification(event);
            } catch (Exception e) {
                log.error("Не удалось получить данные пользователя {}: {}", event.assignedUserId(), e.getMessage());
            }
        }
    }

}
