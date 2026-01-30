package com.project.userservice.utils;


import user.model.User;
import com.project.userservice.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toDomainEntity(UserEntity entity);
    UserEntity toEntity(User user);
}
