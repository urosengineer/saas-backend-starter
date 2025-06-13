package com.urke.saasbackendstarter.exception;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException(String message) {
        super(message);
    }
}