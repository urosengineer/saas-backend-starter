package com.urke.saasbackendstarter.exception;

public class AuthException extends RuntimeException {
    public AuthException(String msg) {
        super(msg);
    }
}