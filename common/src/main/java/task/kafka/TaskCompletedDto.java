package task.kafka;

public record TaskCompletedDto(
        Long taskId,
        Long creatorId,
        Long assignedUserId
) {
}
