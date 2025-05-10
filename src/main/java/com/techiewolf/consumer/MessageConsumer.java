package com.techiewolf.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @KafkaListener(topics = "${app.kafka.topics.notification-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(String notification) {
        logger.info("Received notification: {}", notification);
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePayment(String payment) {
        logger.info("Received payment: {}", payment);
    }
}
