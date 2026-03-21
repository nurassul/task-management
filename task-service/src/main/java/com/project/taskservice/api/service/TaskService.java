package com.project.taskservice.api.service;

import com.project.taskservice.feign.UserClient;
import com.project.taskservice.repository.TaskRepository;
import com.project.taskservice.repository.entity.TaskEntity;
import com.project.taskservice.utils.TaskMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.kafka.TaskEvent;
import task.kafka.TaskEventType;
import task.model.Priority;
import task.model.TaskDto;
import task.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@Service
public class TaskService {

    private final KafkaTemplate<Long, TaskEvent> kafkaTemplate;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserClient userClient;

    public TaskDto findTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        return taskMapper.toDomainTask(task);
    }

    public List<TaskDto> findAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toDomainTask)
                .toList();
    }

    /*
            CREATING TASK
        !!!EVENT TYPE - CREATED!!!
     */
    public TaskDto createTask(TaskDto taskDtoToCreate) {
        if (taskDtoToCreate.taskStatus() != null) {
            throw new IllegalArgumentException("Status should be empty!");
        }

        validateUserExists(taskDtoToCreate.creatorId());
        validateUserExists(taskDtoToCreate.assignedUserId());

        var entityToSave = taskMapper.toEntity(taskDtoToCreate);
        entityToSave.setTaskStatus(TaskStatus.CREATED);
        entityToSave.setCreateDateTime(LocalDate.now());

        validateDeadline(entityToSave.getDeadlineDate(), entityToSave.getCreateDateTime());

        var updatedEntity = taskRepository.save(entityToSave);
        sendTaskEvent(updatedEntity, null, null, TaskEventType.CREATED);
        return taskMapper.toDomainTask(updatedEntity);
    }

    /*
            DELETING TASK
         EVENT TYPE - DELETED
    */
    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        taskRepository.delete(task);
        sendTaskEventForDelete(task);
        log.info("Task with id= {} was deleted", id);
    }

    /*
                UPDATING TASK
             EVENT TYPE - UPDATED
     */
    public TaskDto updateTask(Long id, TaskDto taskDtoToUpdate) {
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found task with id=" + id));

        TaskStatus oldStatus = existingTask.getTaskStatus();
        Priority oldPriority = existingTask.getPriority();

        if (taskDtoToUpdate.creatorId() == null) {
            throw new IllegalArgumentException("creatorId is required");
        }

        if (taskDtoToUpdate.deadlineDate() == null) {
            throw new IllegalArgumentException("deadlineDate is required");
        }

        if (taskDtoToUpdate.taskStatus() == null) {
            throw new IllegalArgumentException("taskStatus is required");
        }

        validateDeadline(taskDtoToUpdate.deadlineDate(), existingTask.getCreateDateTime());
        validateUserExists(taskDtoToUpdate.creatorId());
        validateUserExists(taskDtoToUpdate.assignedUserId());

        TaskStatus currentStatus = existingTask.getTaskStatus();
        TaskStatus requestedStatus = taskDtoToUpdate.taskStatus();

        validateStatusTransition(currentStatus, requestedStatus);

        if (currentStatus == TaskStatus.DONE) {
            boolean isOnlyStatusChange = requestedStatus == TaskStatus.IN_PROGRESS
                    && Objects.equals(taskDtoToUpdate.deadlineDate(), existingTask.getDeadlineDate())
                    && Objects.equals(taskDtoToUpdate.assignedUserId(), existingTask.getAssignedUserId())
                    && Objects.equals(taskDtoToUpdate.creatorId(), existingTask.getCreatorId());

            if (!isOnlyStatusChange) {
                throw new IllegalStateException("Cannot modify task! status: " + currentStatus);
            }
        } else {
            existingTask.setCreatorId(taskDtoToUpdate.creatorId());
            existingTask.setAssignedUserId(taskDtoToUpdate.assignedUserId());
            existingTask.setDeadlineDate(taskDtoToUpdate.deadlineDate());
            existingTask.setPriority(taskDtoToUpdate.priority());
        }

        existingTask.setTaskStatus(requestedStatus);
        if (requestedStatus == TaskStatus.DONE && currentStatus != TaskStatus.DONE) {
            existingTask.setDoneDateTime(LocalDateTime.now());
        } else if (requestedStatus != TaskStatus.DONE) {
            existingTask.setDoneDateTime(null);
        }

        TaskEntity savedTask = taskRepository.save(existingTask);
        sendTaskEvent(savedTask, oldStatus, oldPriority, TaskEventType.UPDATED);
        return taskMapper.toDomainTask(savedTask);
    }

    /*
                UPDATING TASK
             EVENT TYPE - UPDATED
     */
    @Transactional
    public TaskDto startTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        if (task.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Task cannot be started, assignedUserId is null!");
        }

        if (task.getTaskStatus() != TaskStatus.CREATED) {
            throw new IllegalStateException("Only CREATED tasks can be started");
        }

        validateUserExists(task.getAssignedUserId());

        Long assignedUserId = task.getAssignedUserId();
        int taskCount = taskRepository.countByAssignedUserIdAndTaskStatus(assignedUserId, TaskStatus.IN_PROGRESS);

        if (taskCount >= 4) {
            throw new IllegalStateException("Limit exceeded (4) with active tasks with userId= " + assignedUserId);
        }

        TaskStatus oldStatus = task.getTaskStatus();
        Priority oldPriority = task.getPriority();
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        var savedTask = taskRepository.save(task);

        sendTaskEvent(savedTask, oldStatus, oldPriority, TaskEventType.UPDATED);
        return taskMapper.toDomainTask(savedTask);
    }

    /*
                UPDATING TASK
             EVENT TYPE - UPDATED
     */
    @Transactional
    public TaskDto completeTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        if (task.getAssignedUserId() == null || task.getDeadlineDate() == null) {
            throw new IllegalArgumentException("Task cannot be done!");
        }

        if (task.getTaskStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS tasks can be completed");
        }

        validateUserExists(task.getAssignedUserId());

        TaskStatus oldStatus = task.getTaskStatus();
        Priority oldPriority = task.getPriority();
        task.setTaskStatus(TaskStatus.DONE);
        task.setDoneDateTime(LocalDateTime.now());

        var savedTask = taskRepository.save(task);

        sendTaskEvent(savedTask, oldStatus, oldPriority, TaskEventType.UPDATED);
        return taskMapper.toDomainTask(savedTask);
    }

    private void sendTaskEventForDelete(TaskEntity task) {
        var event = new TaskEvent(
                task.getId(),
                task.getCreatorId(),
                task.getAssignedUserId(),
                task.getTaskStatus(),
                null,
                task.getPriority(),
                null,
                LocalDateTime.now(),
                TaskEventType.DELETED
        );
        kafkaTemplate.send("task-events", task.getId(), event);
    }

    private void sendTaskEvent(TaskEntity task, TaskStatus oldStatus, Priority oldPriority, TaskEventType eventType) {
        var event = new TaskEvent(
                task.getId(),
                task.getCreatorId(),
                task.getAssignedUserId(),
                oldStatus,
                task.getTaskStatus(),
                oldPriority,
                task.getPriority(),
                LocalDateTime.now(),
                eventType
        );

        kafkaTemplate.send("task-events", task.getId(), event);
    }


    private void validateDeadline(LocalDate deadlineDate, LocalDate createDate) {
        if (deadlineDate == null) {
            throw new IllegalArgumentException("Deadline date is required");
        }

        if (!deadlineDate.isAfter(createDate)) {
            throw new IllegalArgumentException("Deadline date must be after create date");
        }
    }

    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus requestedStatus) {
        if (currentStatus == requestedStatus) {
            return;
        }

        boolean isAllowed = (currentStatus == TaskStatus.CREATED && requestedStatus == TaskStatus.IN_PROGRESS)
                || (currentStatus == TaskStatus.IN_PROGRESS && requestedStatus == TaskStatus.DONE)
                || (currentStatus == TaskStatus.DONE && requestedStatus == TaskStatus.IN_PROGRESS);

        if (!isAllowed) {
            throw new IllegalStateException(
                    "Unsupported status transition: " + currentStatus + " -> " + requestedStatus
            );
        }
    }

    private void validateUserExists(Long userId) {
        if (userId == null) {
            return;
        }

        Boolean exists = userClient.checkUserExisting(userId);
        if (!Boolean.TRUE.equals(exists)) {
            throw new EntityNotFoundException("User with id= " + userId + " not found");
        }
    }
}
