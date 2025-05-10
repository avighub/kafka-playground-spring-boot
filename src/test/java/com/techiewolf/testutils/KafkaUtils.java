package com.techiewolf.testutils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class KafkaUtils {
    private static final Logger log = LoggerFactory.getLogger(KafkaUtils.class);
    private final String bootstrapServers = "localhost:9092";
    private final String groupId = "test-group";

    private KafkaUtils() {
    }

    public static KafkaConsumer<String, String> createConsumer(String bootstrapServers, String groupId, String offsetReset) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        return new KafkaConsumer<>(props);
    }

    public static void subscribeToTopic(KafkaConsumer<String, String> consumer, String topic) {
        consumer.subscribe(Collections.singletonList(topic));

        // Wait until the consumer is assigned partitions
        long startTime = System.currentTimeMillis();
        while (consumer.assignment().isEmpty()) {
            if (System.currentTimeMillis() - startTime > 10000) { // 10-second timeout
                throw new RuntimeException("Timeout waiting for partitions to be assigned");
            }
            log.info("Waiting for partitions to be assigned for consumer...");
            consumer.poll(Duration.ofSeconds(1));
        }
        log.info("Subscribed to topic: {}", topic);
    }

    public static List<String> consumeMessage(
            KafkaConsumer<String, String> consumer,
            int expectedMessageCount,
            List<String> matchStringsInMessages,
            Duration timeout) {
        List<String> mutableMatchStrings = new ArrayList<>(matchStringsInMessages);
        List<String> matchingMessages = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        long maxTimeoutMillis = timeout.toMillis();

        while (matchingMessages.size() < expectedMessageCount) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, String> record : records) {
                for (String matchString : new ArrayList<>(mutableMatchStrings)) {
                    if (record.value().contains(matchString)) {
                        matchingMessages.add(record.value());
                        mutableMatchStrings.remove(matchString);
                        break;
                    }
                }
                if (matchingMessages.size() == expectedMessageCount) {
                    break;
                }
            }

            if (System.currentTimeMillis() - startTime > maxTimeoutMillis) {
                break;
            }
        }
        log.info("Consumed {} messages from topic. Messages : {}", matchingMessages.size(), matchingMessages);

        return matchingMessages;
    }
}
