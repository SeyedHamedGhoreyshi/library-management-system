package com.library.core.application.port.exception;


public class DuplicateIsbnException extends ApplicationException {

    private final String isbn;

    public DuplicateIsbnException(String isbn) {
        super("A book with ISBN [" + isbn + "] already exists in the catalog");
        this.isbn = isbn;
    }

    public DuplicateIsbnException(String isbn, Throwable cause) {
        super("A book with ISBN [" + isbn + "] already exists in the catalog", cause);
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
