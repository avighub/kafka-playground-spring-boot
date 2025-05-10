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

//    @PostMapping("/send-message")
//    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {
//        eventProducer.sendMessage(messageDto.getTopic(),
//                messageDto.getKey(),
//                messageDto.getValue());
//        return ResponseEntity.ok("Message sent to Kafka topic: " + messageDto.getTopic());
//    }
//
//    @PostMapping("/send-messages")
//    public ResponseEntity<String> sendMessages(@RequestBody List<MessageDto> messages) {

    /// /        messages.forEach(
    /// /                message -> eventProducer.sendMessage(
    /// /                        message.getTopic(),
    /// /                        message.getKey(),
    /// /                        message.getValue())
    /// /        );
//        eventProducer.sendMessage(messages.get(0).getTopic(), messages.get(0).getKey(), messages.get(0).getValue());
//        return ResponseEntity.ok("Message(s) sent to Kafka topic(s)");
//    }
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
//        String key = UUID.randomUUID().toString();
//        eventProducer.sendMessage(notificationTopic, key, orderDto);
//        log.info("Message sent to topic: " + notificationTopic);

//        List<MessageDto> paymentMessages = orders.stream().map(order -> new MessageDto(paymentTopic, UUID.randomUUID().toString(), order.toString())).toList();
//        sendMessages(paymentMessages);
//        log.info("Message sent to topic: " + paymentTopic);

        return ResponseEntity.ok(orders);
    }
}
