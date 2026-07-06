package com.library.infrastructure.security.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("A user with email [" + email + "] is already registered");
    }
}
