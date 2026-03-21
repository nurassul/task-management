package com.project.statisticsservice.utils;


import com.project.statisticsservice.api.dto.task.TaskStatsDto;
import com.project.statisticsservice.api.dto.user.UserStatsDto;
import com.project.statisticsservice.repository.entity.TaskStatsEntity;
import com.project.statisticsservice.repository.entity.UserStatsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserStatsMapper {

    UserStatsDto toDomainTask(UserStatsEntity entity);
    UserStatsEntity toEntity(UserStatsDto user);

}
