package com.techiewolf.integrationtests.notification;

import com.techiewolf.dto.OrderDto;
import com.techiewolf.testutils.APIUtils;
import com.techiewolf.testutils.KafkaTestConsumerManager;
import com.techiewolf.testutils.KafkaUtils;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NotificationTopicTest {
    private static KafkaConsumer<String, String> notificationConsumer;
    @Autowired
    private APIUtils apiUtils;
    @Autowired
    private KafkaTestConsumerManager kafkaTestConsumerManager;

    @BeforeAll
    public static void setUp(@Autowired KafkaTestConsumerManager kafkaTestConsumerManager) {
        String offsetReset = "latest";
        notificationConsumer = kafkaTestConsumerManager.createNotificationConsumer(offsetReset);
    }

    @AfterAll
    public static void tearDown() {
        if (notificationConsumer != null) {
            notificationConsumer.close();
        }
    }

    @Test
    @Tag("integration-test")
    @Tag("notifications-topic")
    public void testSingleNotificationMessage() {
        List<OrderDto> orders = List.of(OrderDto.getInstance());
        List<String> transactionIds = orders.stream().map(OrderDto::getTransactionId).toList();

        Response response = apiUtils.sendMessageToNotificationsTopic(orders);
        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedMessages =
                KafkaUtils.consumeMessage(notificationConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedMessages);
        assertTrue(consumedMessages.stream().allMatch(
                id -> consumedMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }

    @Test
    @Tag("integration-test")
    @Tag("notifications-topic")
    public void testMultiNotificationMessages() {
        List<OrderDto> orders = List.of(OrderDto.getInstance(), OrderDto.getInstance());
        List<String> transactionIds = orders.stream().map(OrderDto::getTransactionId).toList();

        Response response = apiUtils.sendMessageToNotificationsTopic(orders);
        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedMessages =
                KafkaUtils.consumeMessage(notificationConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedMessages);
        assertTrue(consumedMessages.stream().allMatch(
                id -> consumedMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }

}
