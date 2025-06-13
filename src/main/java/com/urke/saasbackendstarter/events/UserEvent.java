package com.urke.saasbackendstarter.events;

import com.urke.saasbackendstarter.domain.User;
import org.springframework.context.ApplicationEvent;

public class UserEvent extends ApplicationEvent {
    public enum Type { REGISTERED, UPDATED, DELETED }

    private final Type type;
    private final User user;

    public UserEvent(Object source, Type type, User user) {
        super(source);
        this.type = type;
        this.user = user;
    }

    public Type getType() { return type; }
    public User getUser() { return user; }
}
