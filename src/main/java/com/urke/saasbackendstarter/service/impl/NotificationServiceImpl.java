package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.dto.NotificationMessage;
import com.urke.saasbackendstarter.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NotificationService.
 * Integrates with WebSocket, and provides placeholders for email/SMS.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[EMAIL] to: {}, subject: {}, body: {}", to, subject, body);
    }

    @Override
    public void sendSms(String to, String message) {
        // TODO: Integrate with real SMS provider
        log.info("[SMS] to: {}, message: {}", to, message);
    }

    @Override
    public void sendWebNotification(Long userId, String message) {
        messagingTemplate.convertAndSend("/queue/user-" + userId, new NotificationMessage(message));
    }
}