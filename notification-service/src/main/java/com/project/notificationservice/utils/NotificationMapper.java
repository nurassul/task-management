package com.project.notificationservice.utils;


import com.project.notificationservice.api.mongodb.NotificationLog;
import org.springframework.stereotype.Component;
import task.kafka.TaskEvent;

@Component
public class NotificationMapper {

    public NotificationLog eventToEntity(TaskEvent event) {
        return NotificationLog.builder()
                .taskId(event.taskId())
                .creatorId(event.creatorId())
                .assignedUserId(event.assignedUserId())
                .oldStatus(event.oldStatus())
                .newStatus(event.newStatus())
                .timestamp(event.timestamp())
                .build();
    }
}
