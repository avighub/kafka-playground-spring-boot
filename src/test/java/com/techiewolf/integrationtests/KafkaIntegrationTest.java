package com.techiewolf.integrationtests;

import com.techiewolf.dto.MessageDto;
import com.techiewolf.testutils.KafkaTestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaIntegrationTest {

    private static final String bootstrapServers = "localhost:9092";
    private static KafkaConsumer<String, String> messageConsumer;
    private static KafkaConsumer<String, String> notificationConsumer;
    private static KafkaConsumer<String, String> auditConsumer;

    @BeforeAll
    public static void setUp() {
        messageConsumer = KafkaTestUtils.createConsumer(bootstrapServers, "message-test-group");
        notificationConsumer = KafkaTestUtils.createConsumer(bootstrapServers, "notification-test-group");
        auditConsumer = KafkaTestUtils.createConsumer(bootstrapServers, "audit-test-group");
        RestAssured.baseURI = "http://localhost:8080";

        // Subscribe to all topics
        KafkaTestUtils.subscribeToTopic(messageConsumer, "messages");
        KafkaTestUtils.subscribeToTopic(notificationConsumer, "notifications");
        KafkaTestUtils.subscribeToTopic(auditConsumer, "audit-logs");
    }

    @AfterAll
    public static void tearDown() {
        if (messageConsumer != null) {
            messageConsumer.close();
        }
        if (notificationConsumer != null) {
            notificationConsumer.close();
        }
        if (auditConsumer != null) {
            auditConsumer.close();
        }
    }

    @Test
    @Tag("integration-test")
    @Tag("message-topic")
    public void testMessageTopicEndToEnd() {
        String topic = "messages";

        // Send message via endpoint
        MessageDto messageDto = new MessageDto("test-key", "test-value", topic);
        Response response = given()
                .contentType("application/json")
                .body(messageDto)
                .post("/api/v1/messages/send");

        assertEquals(200, response.getStatusCode());

        // Validate message
        String consumedMessage = KafkaTestUtils.consumeMessage(messageConsumer, topic, Duration.ofSeconds(10));
        assertEquals("test-value", consumedMessage);
    }

    @Test
    @Tag("integration-test")
    @Tag("notification-topic")
    public void testNotificationTopicEndToEnd() {
        String topic = "notifications";

        // Send notification via endpoint
        String notification = "test-notification";
        Response response = given()
                .contentType("application/json")
                .body(notification)
                .post("/api/v1/messages/send/notification-topic");

        assertEquals(200, response.getStatusCode());

        // Validate notification
        String consumedMessage = KafkaTestUtils.consumeMessage(notificationConsumer, topic, Duration.ofSeconds(10));
        assertEquals(notification, consumedMessage);
    }

    @Test
    @Tag("integration-test")
    @Tag("audit-topic")
    public void testAuditTopicEndToEnd() {
        String topic = "audit-logs";

        // Send audit log via endpoint
        String auditLog = "test-audit-log";
        Response response = given()
                .contentType("application/json")
                .body(auditLog)
                .post("/api/v1/messages/send/audit-topic");

        assertEquals(200, response.getStatusCode());

        // Validate audit log
        String consumedMessage = KafkaTestUtils.consumeMessage(auditConsumer, topic, Duration.ofSeconds(10));
        assertEquals(auditLog, consumedMessage);
    }
}
