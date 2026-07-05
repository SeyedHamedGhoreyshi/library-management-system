package com.library.core.domain.exception;

/**
 * Thrown when an actor attempts a domain operation they are not
 * authorized to perform based on their {@link com.library.core.domain.model.Role}.
 */
public class UnauthorizedDomainActionException extends DomainException {

    private static final String DEFAULT_MESSAGE =
        "The actor is not authorized to perform this domain action.";

    /** Throws with a standard default message. */
    public UnauthorizedDomainActionException() {
        super(DEFAULT_MESSAGE);
    }

    /** Throws with a custom context message naming the required role or action. */
    public UnauthorizedDomainActionException(String message) {
        super(message);
    }

    /**
     * Throws with a custom context message and an underlying cause.
     */
    public UnauthorizedDomainActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
