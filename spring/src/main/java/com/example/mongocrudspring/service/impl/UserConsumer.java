package com.example.mongocrudspring.service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {

    @KafkaListener(topics = "user-events", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Consumed user event: " + message);
    }
}
