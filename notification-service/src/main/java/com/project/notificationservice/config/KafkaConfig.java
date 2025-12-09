package com.project.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Spring Boot автоматически создаст beans на основе application.yml
}
