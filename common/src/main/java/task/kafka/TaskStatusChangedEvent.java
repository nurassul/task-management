package task.kafka;

import task.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskStatusChangedEvent(
        Long taskId,
        Long creatorId,
        Long assignedUserId,
        TaskStatus oldStatus,
        TaskStatus newStatus,
        LocalDateTime timestamp
) {
}
