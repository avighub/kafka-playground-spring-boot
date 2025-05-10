package com.techiewolf.controller;

import com.techiewolf.dto.OrderDto;
import com.techiewolf.producer.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private static final Logger log = Logger.getLogger(OrderController.class.getName());
    private final EventProducer eventProducer;

    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    @Value("${app.kafka.topics.payment-topic}")
    private String paymentTopic;

    @Autowired
    public OrderController(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostMapping("/send/notification-topic")
    public ResponseEntity<String> sendToNotificationTopic(@RequestBody List<OrderDto> orders) {
        orders.forEach(
                order -> eventProducer.sendMessage(
                        notificationTopic,
                        UUID.randomUUID().toString(),
                        order)
        );
        return ResponseEntity.ok("Notification sent to topic: " + notificationTopic);
    }

    @PostMapping("/send/payment-topic")
    public ResponseEntity<String> sendToPaymentTopic(@RequestBody List<OrderDto> orders) {
        orders.forEach(
                order -> eventProducer.sendMessage(
                        paymentTopic,
                        UUID.randomUUID().toString(),
                        order)
        );
        return ResponseEntity.ok("Payment log sent to topic: " + paymentTopic);
    }

    @PostMapping("/create")
    public ResponseEntity<List<OrderDto>> createOrder(@RequestBody List<OrderDto> orders) {
        sendToNotificationTopic(orders);
        sendToPaymentTopic(orders);

        return ResponseEntity.ok(orders);
    }
}
