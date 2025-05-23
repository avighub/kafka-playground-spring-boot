package com.techiewolf.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public EventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String key, Object message) {
        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, key, jsonMessage);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Sent message=[{}] with offset=[{}]",
                            jsonMessage, result.getRecordMetadata().offset());
                } else {
                    logger.error("Unable to send message=[{}] due to: {}",
                            jsonMessage, ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Failed to convert message to JSON: {}", e.getMessage());
        }

    }
}
