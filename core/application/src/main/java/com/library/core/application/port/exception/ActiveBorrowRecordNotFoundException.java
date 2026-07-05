package com.library.core.application.port.exception;


public class ActiveBorrowRecordNotFoundException extends ApplicationException {

    private final Long bookId;

    public ActiveBorrowRecordNotFoundException(Long bookId) {
        super("No active borrow record found for book with id [" + bookId + "]");
        this.bookId = bookId;
    }

    public ActiveBorrowRecordNotFoundException(Long bookId, Throwable cause) {
        super("No active borrow record found for book with id [" + bookId + "]", cause);
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }
}
