package com.library.core.domain.exception;

/**
 * Thrown when an actor attempts a domain operation they are not
 * authorized to perform based on their {@link com.library.core.domain.model.Role}.
 */
public class UnauthorizedDomainActionException extends DomainException {
    private static final String DEFAULT_MESSAGE = "The actor is not authorized to perform this domain action.";

    public UnauthorizedDomainActionException() {
        super(DEFAULT_MESSAGE);
    }

    public UnauthorizedDomainActionException(String message) {
        super(message);
    }
}