package com.project.taskservice.utils;


import com.project.taskservice.api.model.TaskDto;
import com.project.taskservice.repository.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto toDomainTask(
            TaskEntity taskEntity
    ) {

        return new TaskDto(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getTaskStatus(),
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                taskEntity.getDoneDateTime()
        );
    }

    public TaskEntity toEntity(
            TaskDto taskDto
    ) {
        return new TaskEntity(
                taskDto.id(),
                taskDto.creatorId(),
                taskDto.assignedUserId(),
                taskDto.taskStatus(),
                taskDto.createDateTime(),
                taskDto.deadlineDate(),
                taskDto.priority(),
                taskDto.doneDateTime()
        );
    }


}
