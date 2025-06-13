package com.urke.saasbackendstarter.exception;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}