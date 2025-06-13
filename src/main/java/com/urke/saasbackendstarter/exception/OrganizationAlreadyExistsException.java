package com.urke.saasbackendstarter.exception;

public class OrganizationAlreadyExistsException extends RuntimeException {
    public OrganizationAlreadyExistsException(String message) {
        super(message);
    }
}