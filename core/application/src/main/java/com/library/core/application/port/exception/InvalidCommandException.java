package com.library.core.application.port.exception;


public class InvalidCommandException extends ApplicationException {

    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
