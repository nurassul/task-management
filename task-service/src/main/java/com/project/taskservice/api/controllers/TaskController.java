package com.project.taskservice.api.controllers;

import com.project.taskservice.api.model.TaskDto;
import com.project.taskservice.api.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // get all Tasks
    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        log.info("Called getAllTasks()");

        return ResponseEntity.ok(taskService.findAllTasks());
    }

    // get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable("id") Long id)
    {
        log.info("Called getTaskById(): id = {}", id);

        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    // create Task
    @PostMapping()
    public ResponseEntity<TaskDto> createTask(
            @RequestBody @Valid TaskDto taskDtoToCreate
    ) {
        log.info("Called createTask()");

        TaskDto created = taskService.createTask(taskDtoToCreate);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // update task by id
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable("id") Long id,
            @RequestBody TaskDto taskDtoToUpdate
    ) {
        log.info("Called updateTask: id={}, taskToUpdate={}", id, taskDtoToUpdate);

        var updated = taskService.updateTask(id, taskDtoToUpdate);

        return ResponseEntity.ok(updated);
    }

    // delete task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") Long id
    ) {
        log.info("Called deleteTask: id={}", id);

        taskService.deleteTask(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TaskDto> startTask(
            @PathVariable("id") Long id
    ){
        log.info("Called change task to IN_PRORESS: id={}", id);

        TaskDto startedTaskDto = taskService.startTask(id);
        return ResponseEntity.ok(startedTaskDto);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(
        @PathVariable("id") Long id
    ){
        TaskDto completedTaskDto = taskService.completeTask(id);

        return ResponseEntity.ok(completedTaskDto);
    }



}
