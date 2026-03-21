package com.project.statisticsservice.api.dto.task;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskStatsDto {
    private long totalCreated;
    private long totalInProgress;
    private long totalDone;

    private long lowPriorityCount;
    private long mediumPriorityCount;
    private long highPriorityCount;
}
