package com.project.statisticsservice.repository;

import com.project.statisticsservice.repository.entity.UserStatsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatsEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.totalCreated = s.totalCreated + :delta WHERE s.userId = :userId")
    void updateTotalCreated(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.totalAssigned = s.totalAssigned + :delta WHERE s.userId = :userId")
    void updateTotalAssigned(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.todoCount = s.todoCount + :delta WHERE s.userId = :userId")
    void updateTodoCount(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.inProgressCount = s.inProgressCount + :delta WHERE s.userId = :userId")
    void updateInProgressCount(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.doneCount = s.doneCount + :delta WHERE s.userId = :userId")
    void updateDoneCount(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.highPriorityCount = s.highPriorityCount + :delta WHERE s.userId = :userId")
    void updateHighPriorityCount(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.mediumPriorityCount = s.mediumPriorityCount + :delta WHERE s.userId = :userId")
    void updateMediumPriorityCount(Long userId, int delta);

    @Modifying
    @Transactional
    @Query("UPDATE UserStatsEntity s SET s.lowPriorityCount = s.lowPriorityCount + :delta WHERE s.userId = :userId")
    void updateLowPriorityCount(Long userId, int delta);


    Optional<UserStatsEntity> findByUserId(Long userId);
}
