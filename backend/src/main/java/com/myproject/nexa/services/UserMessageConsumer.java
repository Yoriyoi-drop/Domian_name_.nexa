package com.myproject.nexa.services;

import com.myproject.nexa.dto.message.UserMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserMessageConsumer {

    @Value("${app.rabbitmq.user-queue}")
    private String userQueue;

    @RabbitListener(queues = "${app.rabbitmq.user-queue}")
    public void processUserMessage(UserMessageDTO userMessage) {
        log.info("Processing user message from queue: {}", userMessage);
        
        // Process the user message based on action
        switch (userMessage.getAction()) {
            case "CREATE":
                handleUserCreated(userMessage);
                break;
            case "UPDATE":
                handleUserUpdated(userMessage);
                break;
            case "DELETE":
                handleUserDeleted(userMessage);
                break;
            default:
                log.warn("Unknown action in user message: {}", userMessage.getAction());
        }
    }

    private void handleUserCreated(UserMessageDTO userMessage) {
        log.info("Handling user creation: User ID {} with username {}", userMessage.getId(), userMessage.getUsername());
        // Here we could perform background tasks like:
        // - Sending welcome email
        // - Updating analytics
        // - Syncing with external systems
        // - Logging user creation for monitoring
    }

    private void handleUserUpdated(UserMessageDTO userMessage) {
        log.info("Handling user update: User ID {} with username {}", userMessage.getId(), userMessage.getUsername());
        // Here we could perform background tasks like:
        // - Sending notification of profile update
        // - Updating external systems
        // - Updating user search index
    }

    private void handleUserDeleted(UserMessageDTO userMessage) {
        log.info("Handling user deletion: User ID {} with username {}", userMessage.getId(), userMessage.getUsername());
        // Here we could perform background tasks like:
        // - Anonymizing user data
        // - Notifying external systems
        // - Updating analytics
    }
}