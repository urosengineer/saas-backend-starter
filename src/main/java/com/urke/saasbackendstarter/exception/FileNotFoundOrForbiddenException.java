package com.urke.saasbackendstarter.exception;

public class FileNotFoundOrForbiddenException extends RuntimeException {
    public FileNotFoundOrForbiddenException(String message) {
        super(message);
    }
}