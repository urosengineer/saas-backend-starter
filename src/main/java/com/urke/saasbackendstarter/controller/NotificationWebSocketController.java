package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket controller for sending real-time notifications.
 */
@RestController
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Receives a notification from the client and broadcasts it to all subscribers.
     * Endpoint: /app/notify → /topic/notifications
     *
     * @param message notification message from client
     * @return the same message, broadcast to all
     */
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public NotificationMessage sendNotification(NotificationMessage message) {
        return message;
    }

    /**
     * Sends a notification to a specific user (private queue).
     * Can be called from any service.
     *
     * @param userId  recipient user ID
     * @param content notification message content
     */
    public void sendNotificationToUser(Long userId, String content) {
        messagingTemplate.convertAndSend("/queue/user-" + userId, new NotificationMessage(content));
    }
}