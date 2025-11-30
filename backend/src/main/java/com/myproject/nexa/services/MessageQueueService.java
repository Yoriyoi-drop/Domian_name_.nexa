package com.myproject.nexa.services;

import com.myproject.nexa.dto.message.UserMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageQueueService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.user-exchange}")
    private String userExchange;

    @Value("${app.rabbitmq.user-routing-key}")
    private String userRoutingKey;

    public void sendUserMessage(UserMessageDTO userMessage) {
        try {
            userMessage.setTimestamp(java.time.LocalDateTime.now());
            rabbitTemplate.convertAndSend(userExchange, userRoutingKey, userMessage);
            log.info("User message sent to queue: {}", userMessage);
        } catch (Exception e) {
            log.error("Error sending user message to queue: {}", e.getMessage(), e);
            throw e;
        }
    }
}