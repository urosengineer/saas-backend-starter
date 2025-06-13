package com.urke.saasbackendstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for WebSocket notification messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String content;
}