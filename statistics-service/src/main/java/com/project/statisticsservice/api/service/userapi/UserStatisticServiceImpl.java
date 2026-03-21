package com.project.statisticsservice.api.service.userapi;


import com.project.statisticsservice.api.dto.user.UserStatsDto;
import com.project.statisticsservice.repository.UserStatisticRepository;
import com.project.statisticsservice.repository.entity.UserStatsEntity;
import com.project.statisticsservice.utils.UserStatsMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.kafka.TaskEvent;
import task.kafka.TaskEventType;
import task.model.Priority;
import task.model.TaskStatus;


@RequiredArgsConstructor
@Service
public class UserStatisticServiceImpl implements UserStatisticService {

    private final UserStatisticRepository repository;
    private final UserStatsMapper mapper;

    @Override
    public void processUserStats(TaskEvent event) {
        Long creatorUserId = event.creatorId();
        Long assignedUserId = event.assignedUserId();

        if (creatorUserId != null) {
            ensureUserExists(creatorUserId);

            if (event.eventType().equals(TaskEventType.CREATED)) {
                repository.updateTotalCreated(creatorUserId, 1);
            } else if (event.eventType().equals(TaskEventType.DELETED)) {
                repository.updateTotalCreated(creatorUserId, -1);
            }
        }

        if (assignedUserId != null) {
            ensureUserExists(assignedUserId);
            handleAssignedLogic(assignedUserId, event);
        }


    }

    @Override
    public UserStatsDto getUserStatsById(Long userId) {
        UserStatsEntity userStatsEntity = repository.findByUserId(userId)
                .orElseThrow(EntityNotFoundException::new);

        return mapper.toDomainTask(userStatsEntity);
    }

    private void handleAssignedLogic(Long userId, TaskEvent event) {
        switch (event.eventType()) {
            case CREATED -> {
                repository.updateTotalAssigned(userId, 1);
                applyStatusChange(userId, event.newStatus(), 1);
                applyPriorityChange(userId,event.newTaskPriority(),1);
            }

            case UPDATED -> {
                if(event.oldStatus() != null && !event.oldStatus().equals(event.newStatus())) {
                    applyStatusChange(userId,event.newStatus(), 1);
                    applyStatusChange(userId,event.oldStatus(), -1);
                }

                if(event.oldTaskPriority() != null && !event.oldTaskPriority().equals(event.newTaskPriority())){
                    applyPriorityChange(userId,event.newTaskPriority(), 1);
                    applyPriorityChange(userId,event.oldTaskPriority(), -1);
                }
            }

            case DELETED -> {
                repository.updateTotalAssigned(userId, -1);
                applyStatusChange(userId, event.newStatus(), -1);
            }
        }
    }

    private void applyStatusChange(Long userId, TaskStatus taskStatus, int delta) {
        if (taskStatus == null) {
            return;
        }

        switch (taskStatus) {
            case CREATED -> repository.updateTodoCount(userId, delta);
            case IN_PROGRESS -> repository.updateInProgressCount(userId, delta);
            case DONE -> repository.updateDoneCount(userId, delta);
        }
    }

    private void applyPriorityChange(Long userId, Priority priority, int delta) {
        if (priority == null) {
            return;
        }

        switch (priority) {
            case LOW -> repository.updateLowPriorityCount(userId, delta);
            case MEDIUM -> repository.updateMediumPriorityCount(userId, delta);
            case HIGH -> repository.updateHighPriorityCount(userId, delta);
        }
    }


    private void ensureUserExists(Long userId) {
        if (repository.findByUserId(userId).isEmpty()) {
            UserStatsEntity userStats = new UserStatsEntity();
            userStats.setUserId(userId);
            repository.save(userStats);
        }
    }
}
