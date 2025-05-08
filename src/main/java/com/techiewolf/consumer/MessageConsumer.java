package com.techiewolf.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @KafkaListener(topics = "${app.kafka.topics.message-topic}", groupId = "my-group")
    public void consumeMessage(String message) {
        logger.info("Received message: {}", message);
    }

    @KafkaListener(topics = "${app.kafka.topics.notification-topic}", groupId = "my-group")
    public void consumeNotification(String notification) {
        logger.info("Received notification: {}", notification);
    }

    @KafkaListener(topics = "${app.kafka.topics.audit-topic}", groupId = "my-group")
    public void consumeAuditLog(String auditLog) {
        logger.info("Received audit log: {}", auditLog);
    }
}
