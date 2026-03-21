package com.project.statisticsservice.api.dto;

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
