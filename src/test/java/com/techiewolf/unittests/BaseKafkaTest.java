package com.techiewolf.unittests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseKafkaTest {

    protected static final String NOTIFICATION_TOPIC = "notifications";
    protected static final String PAYMENT_TOPIC = "payments";
    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    protected String generateTestPayload(String action) {
        String uuid = UUID.randomUUID().toString();
        return String.format("""
                {
                    "timestamp": "%s",
                    "userId": "user-%s",
                    "action": "%s",
                    "entity": "order",
                    "entityId": "%s"
                }""", Instant.now(), uuid, action, uuid);
    }
}
