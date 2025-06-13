package com.urke.saasbackendstarter.service;

/**
 * Service interface for sending notifications via email, SMS, or WebSocket.
 */
public interface NotificationService {
    /**
     * Send an email notification.
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Send an SMS notification.
     */
    void sendSms(String to, String message);

    /**
     * Send a WebSocket notification to a user.
     */
    void sendWebNotification(Long userId, String message);
}