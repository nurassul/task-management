package com.project.notificationservice.api.service;


import com.project.notificationservice.api.mongodb.NotificationLog;
import com.project.notificationservice.api.repository.NotificationRepository;
import com.project.notificationservice.utils.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task.kafka.TaskEvent;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    public void saveNotification(TaskEvent event) {
        NotificationLog savedLog = notificationRepository.save(mapper.eventToEntity(event));

        log.info("Notification was saved: id={}", savedLog.getId());
    }

}
