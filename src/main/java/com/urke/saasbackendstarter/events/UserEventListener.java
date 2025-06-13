package com.urke.saasbackendstarter.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {

    @EventListener
    public void handleUserEvent(UserEvent event) {
        log.info("[EVENT] User {}: id={}, email={}",
                event.getType(), event.getUser().getId(), event.getUser().getEmail());
    }
}
