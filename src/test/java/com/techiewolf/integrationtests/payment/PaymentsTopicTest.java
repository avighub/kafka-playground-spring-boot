package com.techiewolf.integrationtests.payment;

import com.techiewolf.dto.OrderDto;
import com.techiewolf.testutils.APIUtils;
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
public class PaymentsTopicTest {

    private static final String bootstrapServers = "localhost:9092";
    private static final String topic = "payments";
    private static KafkaConsumer<String, String> paymentsConsumer;
    @Autowired
    private APIUtils apiUtils;

    @BeforeAll
    public static void setUp() {
        String offsetReset = "latest";
        paymentsConsumer = KafkaUtils.createConsumer(bootstrapServers, "payments-test-group", offsetReset);
        KafkaUtils.subscribeToTopic(paymentsConsumer, topic);
    }

    @AfterAll
    public static void tearDown() {
        if (paymentsConsumer != null) {
            paymentsConsumer.close();
        }
    }

    @Test
    @Tag("integration-test")
    @Tag("payments-topic")
    public void testSinglePaymentMessage() {
        List<OrderDto> orders = List.of(OrderDto.getInstance());
        List<String> transactionIds = orders.stream()
                .map(OrderDto::getTransactionId)
                .toList();

        Response response = apiUtils.sendMessageToPaymentsTopic(orders);
        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedMessages =
                KafkaUtils.consumeMessage(paymentsConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedMessages);
        assertTrue(consumedMessages.stream().allMatch(
                id -> consumedMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }

    @Test
    @Tag("integration-test")
    @Tag("payments-topic")
    public void testMultiPaymentMessages() {
        List<OrderDto> orders = List.of(OrderDto.getInstance(), OrderDto.getInstance());
        List<String> transactionIds = orders.stream()
                .map(OrderDto::getTransactionId)
                .toList();

        Response response = apiUtils.sendMessageToPaymentsTopic(orders);
        assertEquals(200, response.getStatusCode());

        // Validate message
        List<String> consumedMessages =
                KafkaUtils.consumeMessage(paymentsConsumer, orders.size(), transactionIds, Duration.ofSeconds(10));
        Assertions.assertNotNull(consumedMessages);
        assertTrue(consumedMessages.stream().allMatch(
                id -> consumedMessages.stream().anyMatch(
                        message -> message.contains(id)
                )
        ));
    }

}
