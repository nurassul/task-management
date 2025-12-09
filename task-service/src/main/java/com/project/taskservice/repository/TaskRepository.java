package com.project.taskservice.repository;


import com.project.taskservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    int countByAssignedUserIdAndTaskStatus(Long assignedUserId, TaskStatus status);
}
