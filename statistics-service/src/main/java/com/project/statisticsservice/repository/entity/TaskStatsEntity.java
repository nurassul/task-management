package com.project.statisticsservice.repository.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "global_task_stats")
public class TaskStatsEntity {

    @Id
    private Long id = 1L;

    private long totalCreated = 0;
    private long totalInProgress = 0;
    private long totalDone = 0;

    private long lowPriorityCount = 0;
    private long mediumPriorityCount = 0;
    private long highPriorityCount = 0;

}
