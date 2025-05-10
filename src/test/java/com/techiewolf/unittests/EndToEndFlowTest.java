package com.techiewolf.unittests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e-test")
@Tag("unit-test")
public class EndToEndFlowTest extends BaseKafkaTest {

    private static final CountDownLatch latch = new CountDownLatch(3);
    private static final Map<String, String> consumedMessages = new ConcurrentHashMap<>();

    @KafkaListener(topics = NOTIFICATION_TOPIC, groupId = "e2e-test-group")
    public void listenNotificationTopic(String payload) {
        consumedMessages.put(NOTIFICATION_TOPIC, payload);
        latch.countDown();
    }

    @KafkaListener(topics = PAYMENT_TOPIC, groupId = "e2e-test-group")
    public void listenPaymentTopic(String payload) {
        consumedMessages.put(PAYMENT_TOPIC, payload);
        latch.countDown();
    }

    @Test
    public void testFullFlow() throws Exception {
        // Generate consistent UUID for all messages in this flow
        String flowId = UUID.randomUUID().toString();

        // Send test messages
        kafkaTemplate.send(NOTIFICATION_TOPIC, generateTestPayload("NOTIFY", flowId));
        kafkaTemplate.send(PAYMENT_TOPIC, generateTestPayload("PAYMENT", flowId));

        // Wait for all messages to be consumed
        assertTrue(latch.await(10, TimeUnit.SECONDS));

        // Verify all messages were processed
        assertEquals(2, consumedMessages.size());
        assertTrue(consumedMessages.get(NOTIFICATION_TOPIC).contains(flowId));
        assertTrue(consumedMessages.get(PAYMENT_TOPIC).contains(flowId));
    }

    private String generateTestPayload(String action, String uuid) {
        return String.format("""
                {
                    "timestamp": "%s",
                    "userId": "user-123",
                    "action": "%s",
                    "entity": "order",
                    "entityId": "%s"
                }""", Instant.now(), action, uuid);
    }
}
