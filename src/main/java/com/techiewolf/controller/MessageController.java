package com.techiewolf.controller;

import com.techiewolf.dto.MessageDto;
import com.techiewolf.producer.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final EventProducer eventProducer;

    @Value("${app.kafka.topics.message-topic}")
    private String messageTopic;

    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    @Value("${app.kafka.topics.audit-topic}")
    private String auditTopic;

    @Autowired
    public MessageController(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {
        eventProducer.sendMessage(messageDto.getTopic(),
                messageDto.getKey(),
                messageDto.getValue());
        return ResponseEntity.ok("Message sent to Kafka topic: " + messageDto.getTopic());
    }

    @PostMapping("/send/message-topic")
    public ResponseEntity<String> sendToMessageTopic(@RequestBody String message) {
        String key = UUID.randomUUID().toString();
        eventProducer.sendMessage(messageTopic, key, message);
        return ResponseEntity.ok("Message sent to topic: " + messageTopic);
    }

    @PostMapping("/send/notification-topic")
    public ResponseEntity<String> sendToNotificationTopic(@RequestBody String notification) {
        String key = UUID.randomUUID().toString();
        eventProducer.sendMessage(notificationTopic, key, notification);
        return ResponseEntity.ok("Notification sent to topic: " + notificationTopic);
    }

    @PostMapping("/send/audit-topic")
    public ResponseEntity<String> sendToAuditTopic(@RequestBody String auditLog) {
        String key = UUID.randomUUID().toString();
        eventProducer.sendMessage(auditTopic, key, auditLog);
        return ResponseEntity.ok("Audit log sent to topic: " + auditTopic);
    }
}
