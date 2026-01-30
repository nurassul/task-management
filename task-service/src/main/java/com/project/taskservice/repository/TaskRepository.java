package com.project.taskservice.repository;


import com.project.taskservice.api.model.TaskStatus;
import com.project.taskservice.repository.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    int countByAssignedUserIdAndTaskStatus(Long assignedUserId, TaskStatus status);
}
