package task.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDto(

        @Null
        Long id,

        @NotNull
        Long creatorId,

        Long assignedUserId,

        TaskStatus taskStatus,

        LocalDate createDateTime,

        @NotNull
        @Future
        LocalDate deadlineDate,

        @NotNull
        Priority priority,

        LocalDateTime doneDateTime
) {
}
