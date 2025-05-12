package com.techiewolf.testutils;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestConsumerManager {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    @Value("${app.kafka.topics.payment-topic}")
    private String paymentTopic;

    public KafkaConsumer<String, String> createAndSubscribeConsumer(String groupId, String offsetReset, String topic) {
        KafkaConsumer<String, String> consumer = KafkaUtils.createConsumer(bootstrapServers, groupId, offsetReset);
        KafkaUtils.subscribeToTopic(consumer, topic);
        return consumer;
    }

    public KafkaConsumer<String, String> createNotificationConsumer(String offsetReset) {
        KafkaConsumer<String, String> consumer = KafkaUtils.createConsumer(bootstrapServers, "notifications-test-group", offsetReset);
        KafkaUtils.subscribeToTopic(consumer, notificationTopic);
        return consumer;
    }

    public KafkaConsumer<String, String> createPaymentsConsumer(String offsetReset) {
        KafkaConsumer<String, String> consumer = KafkaUtils.createConsumer(bootstrapServers, "payments-test-group", offsetReset);
        KafkaUtils.subscribeToTopic(consumer, paymentTopic);
        return consumer;
    }
}
