package com.techiewolf.integrationtests.createorder;

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
public class CreateOrderTests {
    private static KafkaConsumer<String, String> notificationConsumer;
    private static KafkaConsumer<String, String> paymentConsumer;
    @Autowired
    private APIUtils apiUtils;
    @Autowired
    private KafkaTestConsumerManager kafkaTestConsumerManager;

    @BeforeAll
    public static void setUp(@Autowired KafkaTestConsumerManager kafkaTestConsumerManager) {
        String offsetReset = "latest";
        notificationConsumer = kafkaTestConsumerManager.createNotificationConsumer(offsetReset);
        paymentConsumer = kafkaTestConsumerManager.createPaymentsConsumer(offsetReset);
    }

    @AfterAll
    public static void tearDown() {
        if (notificationConsumer != null) {
            notificationConsumer.close();
        }
        if (paymentConsumer != null) {
            paymentConsumer.close();
        }
    }

    @Test
    @Tag("integration-test")
    @Tag("create-order")
    public void testSingleOrder() {
        List<OrderDto> orders = List.of(OrderDto.getInstance());
        List<String> transactionIds = orders.stream()
                .map(OrderDto::getTransactionId)
                .toList();

        Response response = apiUtils.createOrders(orders);

        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedNotificationMessages =
                KafkaUtils.consumeMessage(notificationConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedNotificationMessages);
        assertTrue(consumedNotificationMessages.stream().allMatch(
                id -> consumedNotificationMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));

        List<String> consumedPaymentMessages =
                KafkaUtils.consumeMessage(paymentConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedPaymentMessages);
        assertTrue(consumedPaymentMessages.stream().allMatch(
                id -> consumedPaymentMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }

    @Test
    @Tag("integration-test")
    @Tag("create-order")
    public void testMultiOrder() {
        List<OrderDto> orders = List.of(OrderDto.getInstance(), OrderDto.getInstance());
        List<String> transactionIds = orders.stream()
                .map(OrderDto::getTransactionId)
                .toList();

        Response response = apiUtils.createOrders(orders);

        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedNotificationMessages =
                KafkaUtils.consumeMessage(notificationConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedNotificationMessages);
        assertTrue(consumedNotificationMessages.stream().allMatch(
                id -> consumedNotificationMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));

        List<String> consumedPaymentMessages =
                KafkaUtils.consumeMessage(paymentConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedPaymentMessages);
        assertTrue(consumedPaymentMessages.stream().allMatch(
                id -> consumedPaymentMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }
}
