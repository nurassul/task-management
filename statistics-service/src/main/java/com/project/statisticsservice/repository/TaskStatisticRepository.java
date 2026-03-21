package com.project.statisticsservice.repository;

import com.project.statisticsservice.repository.entity.TaskStatsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskStatisticRepository extends JpaRepository<TaskStatsEntity,Long> {


    // INCREMENT STATUS STATS
    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalCreated = s.totalCreated + 1 WHERE s.id = 1")
    void incrementTotalCreated();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalInProgress = s.totalInProgress + 1 WHERE s.id = 1")
    void incrementTotalInProgress();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalDone = s.totalDone + 1 WHERE s.id = 1")
    void incrementTotalDone();

    // DECREMENT STATUS STATS
    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalCreated = s.totalCreated - 1 WHERE s.id = 1")
    void decrementTotalCreated();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalInProgress = s.totalInProgress - 1 WHERE s.id = 1")
    void decrementTotalInProgress();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.totalDone = s.totalDone - 1 WHERE s.id = 1")
    void decrementTotalDone();

    // INCREMENT PRIORITY STATS
    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.lowPriorityCount = s.lowPriorityCount + 1 WHERE s.id = 1")
    void incrementLowPriorityCount();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.mediumPriorityCount = s.mediumPriorityCount + 1 WHERE s.id = 1")
    void incrementMediumPriorityCount();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.highPriorityCount = s.highPriorityCount + 1 WHERE s.id = 1")
    void incrementHighPriorityCount();


    // DECREMENT PRIORITY STATS
    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.lowPriorityCount = s.lowPriorityCount - 1 WHERE s.id = 1")
    void decrementLowPriorityCount();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.mediumPriorityCount = s.mediumPriorityCount - 1 WHERE s.id = 1")
    void decrementMediumPriorityCount();

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatsEntity s SET s.highPriorityCount = s.highPriorityCount - 1 WHERE s.id = 1")
    void decrementHighPriorityCount();

}
