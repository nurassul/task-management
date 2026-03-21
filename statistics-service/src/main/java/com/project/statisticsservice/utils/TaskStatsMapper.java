package com.project.statisticsservice.utils;


import com.project.statisticsservice.api.dto.TaskStatsDto;
import com.project.statisticsservice.repository.entity.TaskStatsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskStatsMapper {

    TaskStatsDto toDomainTask(TaskStatsEntity entity);
    TaskStatsEntity toEntity(TaskStatsDto user);

}
