package com.techiewolf.unittests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@Tag("message-topic")
@Tag("unit-test")
public class MessageTopicTest extends BaseKafkaTest {

    private static String lastConsumedMessage;

    @KafkaListener(topics = MESSAGE_TOPIC, groupId = "message-test-group")
    public void listen(String payload) {
        lastConsumedMessage = payload;
    }

    @Test
    public void testSingleMessage() throws Exception {
        String payload = generateTestPayload("CREATE");
        kafkaTemplate.send(MESSAGE_TOPIC, payload);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(lastConsumedMessage).isEqualTo(payload)
                );
    }
}
