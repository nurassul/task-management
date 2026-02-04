package com.project.notificationservice.api.repository;

import com.project.notificationservice.api.mongodb.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends MongoRepository<NotificationLog, String> {
}
