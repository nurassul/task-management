package task.kafka;

import task.model.Priority;
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

        Priority oldTaskPriority,
        Priority newTaskPriority,

        LocalDateTime timestamp,
        TaskEventType eventType
) {
}
