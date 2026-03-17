package com.project.taskservice.utils;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import task.model.TaskDto;
import com.project.taskservice.repository.entity.TaskEntity;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    TaskDto toDomainTask(TaskEntity entity);
    TaskEntity toEntity(TaskDto user);

}
