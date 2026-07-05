package com.library.core.domain.exception;

public class BookIsBorrowedException extends DomainException {

    private static final String DEFAULT_MESSAGE =
        "Cannot perform this operation: the book is currently borrowed.";

    /** Throws with a standard default message. */
    public BookIsBorrowedException() {
        super(DEFAULT_MESSAGE);
    }

    /** Throws with a custom context message. */
    public BookIsBorrowedException(String message) {
        super(message);
    }

    /**
     * Throws with a custom context message and an underlying cause.
     */
    public BookIsBorrowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
