package com.project.taskservice.api.service;


import task.kafka.TaskStatusChangedEvent;
import task.model.TaskDto;
import task.model.TaskStatus;
import com.project.taskservice.repository.entity.TaskEntity;
import com.project.taskservice.repository.TaskRepository;
import com.project.taskservice.utils.TaskMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@Service
public class TaskService {

    private final KafkaTemplate<Long, TaskStatusChangedEvent> kafkaTemplate;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final RestClient restClient;

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


    // create Task
    public TaskDto createTask(TaskDto taskDtoToCreate) {
        if (taskDtoToCreate.taskStatus() != null) {
            throw new IllegalArgumentException("Status should be empty!");
        }

        validateUserExists(taskDtoToCreate.creatorId());
        validateUserExists(taskDtoToCreate.assignedUserId());

        var entityToSave = taskMapper.toEntity(taskDtoToCreate);

        entityToSave.setTaskStatus(TaskStatus.CREATED);
        entityToSave.setCreateDateTime(LocalDate.now());

        if (entityToSave.getDeadlineDate().isBefore(entityToSave.getCreateDateTime())) {
            throw new IllegalArgumentException("Deadline date должен быть после даты создания");
        }

        var updatedEntity = taskRepository.save(entityToSave);
        return taskMapper.toDomainTask(updatedEntity);
    }

    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        taskRepository.delete(task);

        log.info("Task with id= {} was deleted", id);
    }

    public TaskDto updateTask(Long id, TaskDto taskDtoToUpdate) {
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Не найдена задача с id=" + id));

        if (!taskDtoToUpdate.deadlineDate().isAfter(taskDtoToUpdate.createDateTime())) {
            throw new IllegalArgumentException("Deadline date должен быть после Create date");
        }

        validateUserExists(taskDtoToUpdate.creatorId());
        validateUserExists(taskDtoToUpdate.assignedUserId());


        if (existingTask.getTaskStatus() == TaskStatus.DONE) {

            boolean isOnlyStatusChange = taskDtoToUpdate.taskStatus() == TaskStatus.IN_PROGRESS &&
                    taskDtoToUpdate.createDateTime().equals(existingTask.getCreateDateTime()) &&
                    taskDtoToUpdate.deadlineDate().equals(existingTask.getDeadlineDate()) &&
                    Objects.equals(taskDtoToUpdate.assignedUserId(), existingTask.getAssignedUserId()) &&
                    Objects.equals(taskDtoToUpdate.creatorId(), existingTask.getCreatorId());

            if (isOnlyStatusChange) {
                existingTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                existingTask.setDoneDateTime(null);
            } else {
                throw new IllegalStateException("Cannot modify task! status: " + existingTask.getTaskStatus());
            }
        } else {
            existingTask.setCreatorId(taskDtoToUpdate.creatorId());
            existingTask.setAssignedUserId(taskDtoToUpdate.assignedUserId());
            existingTask.setTaskStatus(taskDtoToUpdate.taskStatus());
            existingTask.setDeadlineDate(taskDtoToUpdate.deadlineDate());

            if (taskDtoToUpdate.taskStatus() != TaskStatus.DONE) {
                existingTask.setDoneDateTime(null);
            }
        }

        TaskEntity savedTask = taskRepository.save(existingTask);
        return taskMapper.toDomainTask(savedTask);
    }

    @Transactional
    public TaskDto startTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        if(task.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Task cannot be started, assignedUserId is null!");
        }

        validateUserExists(task.getAssignedUserId());

        Long assignedUserId = task.getAssignedUserId();
        int taskCount = taskRepository.countByAssignedUserIdAndTaskStatus(assignedUserId,TaskStatus.IN_PROGRESS);

        if(taskCount >= 4) {
            throw new IllegalStateException("Limit exceeded (4) with active tasks with userId= " + assignedUserId);
        }

        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        var savedTask = taskRepository.save(task);


        var event = new TaskStatusChangedEvent(
                savedTask.getId(),
                savedTask.getCreatorId(),
                savedTask.getAssignedUserId(),
                task.getTaskStatus(),
                savedTask.getTaskStatus(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("task-events", id, event);

        return taskMapper.toDomainTask(savedTask);
    }

    @Transactional
    public TaskDto completeTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found Task with id= " + id));

        if(task.getAssignedUserId() == null || task.getDeadlineDate() == null) {
            throw new IllegalArgumentException("Task cannot be done!");
        }

        validateUserExists(task.getAssignedUserId());

        task.setTaskStatus(TaskStatus.DONE);
        task.setDoneDateTime(LocalDateTime.now());

        var savedTask = taskRepository.save(task);

        var event = new TaskStatusChangedEvent(
                savedTask.getId(),
                savedTask.getCreatorId(),
                savedTask.getAssignedUserId(),
                task.getTaskStatus(),
                savedTask.getTaskStatus(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("task-events", id, event);

        return taskMapper.toDomainTask(savedTask);
    }


    private void validateUserExists(Long userId) {
        if(userId == null) {
            return;
        }
        try {
            restClient.get()
                    .uri("http://user-api:8080/users/private/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("User with id= " + userId + " not found!");
        }
    }
}
