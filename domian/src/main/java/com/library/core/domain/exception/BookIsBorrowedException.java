package com.library.core.domain.exception;

public class BookIsBorrowedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Cannot delete a book that is currently borrowed." ;
    public BookIsBorrowedException(){super(DEFAULT_MESSAGE);}
    public BookIsBorrowedException(String message) {
        super(message);
    }
}
