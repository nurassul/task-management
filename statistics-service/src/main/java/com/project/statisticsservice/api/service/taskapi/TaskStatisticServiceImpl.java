package com.project.statisticsservice.api.service.taskapi;


import com.project.statisticsservice.api.dto.task.TaskStatsDto;
import com.project.statisticsservice.repository.TaskStatisticRepository;
import com.project.statisticsservice.repository.entity.TaskStatsEntity;
import com.project.statisticsservice.utils.TaskStatsMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.kafka.TaskEvent;
import task.kafka.TaskEventType;
import task.model.Priority;
import task.model.TaskStatus;

@RequiredArgsConstructor
@Service
public class TaskStatisticServiceImpl implements TaskStatisticService {

    private final TaskStatisticRepository repository;
    private final TaskStatsMapper mapper;


    @Transactional
    @Override
    public void processEvent(TaskEvent event) {
        // 1. Логика по СТАТУСАМ
        if (event.eventType().equals(TaskEventType.DELETED)) {
            repository.decrementTotalCreated();
            decrementStatus(event.oldStatus());
            decrementPriority(event.oldTaskPriority());
            return;
        }

        switch (event.eventType()) {

            case CREATED -> {
                repository.incrementTotalCreated();

                // PRIORITY
                if (event.oldTaskPriority() != null && !event.oldTaskPriority().equals(event.newTaskPriority())) {
                    decrementPriority(event.oldTaskPriority());
                }
                if (event.oldTaskPriority() == null || !event.oldTaskPriority().equals(event.newTaskPriority())) {
                    incrementPriority(event.newTaskPriority());
                }
            }

            case UPDATED -> {
                // STATUS
                if (!event.oldStatus().equals(event.newStatus())) {
                    decrementStatus(event.oldStatus());
                }
                if (event.oldStatus() == null || !event.oldStatus().equals(event.newStatus())) {
                    incrementStatus(event.newStatus());
                }

                // PRIORITY
                if (event.oldTaskPriority() != null && !event.oldTaskPriority().equals(event.newTaskPriority())) {
                    decrementPriority(event.oldTaskPriority());
                }
                if (event.oldTaskPriority() == null || !event.oldTaskPriority().equals(event.newTaskPriority())) {
                    incrementPriority(event.newTaskPriority());
                }
            }

        }
    }

    @Override
    public TaskStatsDto getAllTaskStatistic() {
        TaskStatsEntity statsEntity = repository.findById(1L)
                .orElseThrow(EntityNotFoundException::new);

        return mapper.toDomainTask(statsEntity);
    }


    private void incrementStatus(TaskStatus status) {
        if (status == null) return;
        switch (status) {
            case IN_PROGRESS -> repository.incrementTotalInProgress();
            case DONE -> repository.incrementTotalDone();
        }
    }

    private void decrementStatus(TaskStatus status) {
        if (status == null) return;
        switch (status) {
            case IN_PROGRESS -> repository.decrementTotalInProgress();
            case DONE -> repository.decrementTotalDone();
        }
    }

    private void incrementPriority(Priority priority) {
        if (priority == null) return;
        switch (priority) {
            case LOW -> repository.incrementLowPriorityCount();
            case MEDIUM -> repository.incrementMediumPriorityCount();
            case HIGH -> repository.incrementHighPriorityCount();
        }
    }

    private void decrementPriority(Priority priority) {
        if (priority == null) return;
        switch (priority) {
            case LOW -> repository.decrementLowPriorityCount();
            case MEDIUM -> repository.decrementMediumPriorityCount();
            case HIGH -> repository.decrementHighPriorityCount();
        }
    }


}
