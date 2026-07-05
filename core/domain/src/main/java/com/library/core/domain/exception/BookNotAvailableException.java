package com.library.core.domain.exception;

/**
 * Thrown when a borrow attempt is made on a book that is either
 * already lent out or has been soft-deleted from the library.
 */
public class BookNotAvailableException extends DomainException {

    private static final String DEFAULT_MESSAGE =
        "The requested book is currently not available for borrowing.";

    /** Throws with a standard default message. */
    public BookNotAvailableException() {
        super(DEFAULT_MESSAGE);
    }

    /** Throws with a custom context message. */
    public BookNotAvailableException(String message) {
        super(message);
    }

    /**
     * Throws with a custom context message and an underlying cause.
     * Use this when wrapping a lower-level exception that triggered the unavailability.
     */
    public BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
