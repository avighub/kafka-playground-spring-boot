package com.techiewolf.unittests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@Tag("notification-topic")
@Tag("unit-test")
public class NotificationTopicTest extends BaseKafkaTest {
    private static String lastConsumedMessage;

    @KafkaListener(topics = NOTIFICATION_TOPIC, groupId = "notification-test-group")
    public void listen(String payload) {
        lastConsumedMessage = payload;
    }

    @Test
    public void testSingleMessage() throws Exception {
        String payload = generateTestPayload("NOTIFY");
        kafkaTemplate.send(NOTIFICATION_TOPIC, payload);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(lastConsumedMessage).isEqualTo(payload)
                );
    }
}
