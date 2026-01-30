package com.project.taskservice.kafka;

public record TaskCompletedDto(
        Long taskId,
        Long creatorId,
        Long assignedUserId
) {
}
