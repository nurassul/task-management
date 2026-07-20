package task.kafka;

import task.model.TaskPriority;
import task.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskEvent(

        Long taskId,

        // user details
        Long creatorId,
        Long assignedUserId,

        // task details
        TaskStatus oldStatus,
        TaskStatus newStatus,

        TaskPriority oldTaskPriority,
        TaskPriority newTaskPriority,

        LocalDateTime timestamp,
        TaskEventType eventType
) {
}
