package com.techiewolf.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    @Value("${app.kafka.topics.payment-topic}")
    private String paymentTopic;

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(notificationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(paymentTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
