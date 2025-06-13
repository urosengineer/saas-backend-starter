package com.urke.saasbackendstarter.exception;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String message) {
        super(message);
    }
}