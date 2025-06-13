package com.urke.saasbackendstarter.events;

import com.urke.saasbackendstarter.domain.Organization;
import org.springframework.context.ApplicationEvent;

/**
 * Application event for organization lifecycle (created, updated, deleted).
 */
public class OrganizationEvent extends ApplicationEvent {
    public enum Type { CREATED, UPDATED, DELETED }

    private final Type type;
    private final Organization organization;

    public OrganizationEvent(Object source, Type type, Organization organization) {
        super(source);
        this.type = type;
        this.organization = organization;
    }

    public Type getType() { return type; }
    public Organization getOrganization() { return organization; }
}