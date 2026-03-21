package com.project.statisticsservice.api.dto.user;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserStatsDto {

    private Long userId;

    private int totalCreated;
    private int totalAssigned;

    private int todoCount;
    private int inProgressCount;
    private int doneCount;

    private int lowPriorityCount;
    private int mediumPriorityCount;
    private int highPriorityCount;

}
