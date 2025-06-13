package com.urke.saasbackendstarter.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Example Spring event listener for organization events.
 * Extend this with audit logging, integrations, or notifications as needed.
 */
@Slf4j
@Component
public class OrganizationEventListener {

    @EventListener
    public void handleOrganizationEvent(OrganizationEvent event) {
        log.info("[EVENT] Organization {}: id={}, name={}",
                event.getType(), event.getOrganization().getId(), event.getOrganization().getName());
    }
}