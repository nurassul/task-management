package com.project.notificationservice.api.mongodb;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import task.model.TaskStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification_log")
public class NotificationLog {

    @Id
    private String id;

    @Field(name = "task_id")
    private Long taskId;

    @Field(name = "creator_id")
    private Long creatorId;

    @Field(name = "assigned_user_id")
    private Long assignedUserId;

    @Field(name = "old_status")
    private TaskStatus oldStatus;

    @Field(name = "new_status")
    private TaskStatus newStatus;

    @Field(name = "time_stamp")
    private LocalDateTime timestamp;

}
