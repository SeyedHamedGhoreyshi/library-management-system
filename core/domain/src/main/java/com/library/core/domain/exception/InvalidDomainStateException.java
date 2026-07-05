package com.library.core.domain.exception;

/**
 * Thrown when a domain operation would put an entity into an invalid
 * or inconsistent state (field validation, returning a non-borrowed book, etc.).
 */
public class InvalidDomainStateException extends DomainException {

    private static final String DEFAULT_MESSAGE =
        "The operation cannot be completed due to an invalid domain state.";

    /** Throws with a standard default message. */
    public InvalidDomainStateException() {
        super(DEFAULT_MESSAGE);
    }

    /** Throws with a custom context message describing the invalid state. */
    public InvalidDomainStateException(String message) {
        super(message);
    }

    /**
     * Throws with a custom context message and an underlying cause.
     */
    public InvalidDomainStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
