package com.urke.saasbackendstarter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DummyNotificationService implements NotificationService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[DUMMY EMAIL] To: {} | Subject: {} | Body: {}", to, subject, body);
    }
    @Override
    public void sendSms(String to, String message) {
        log.info("[DUMMY SMS] To: {} | Message: {}", to, message);
    }
    @Override
    public void sendWebNotification(Long userId, String message) {
        log.info("[DUMMY WEBSOCKET] UserID: {} | Message: {}", userId, message);
    }
}