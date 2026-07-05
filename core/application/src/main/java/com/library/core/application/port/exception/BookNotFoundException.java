package com.library.core.application.port.exception;


public class BookNotFoundException extends ApplicationException {

    private final Long bookId;

    public BookNotFoundException(Long bookId) {
        super("Book not found with id [" + bookId + "]");
        this.bookId = bookId;
    }

    public BookNotFoundException(Long bookId, Throwable cause) {
        super("Book not found with id [" + bookId + "]", cause);
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }
}
