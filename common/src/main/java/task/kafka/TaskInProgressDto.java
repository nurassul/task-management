package task.kafka;

public record TaskInProgressDto(
        Long taskId,
        Long creatorId,
        Long assignedUserId
) {
}
