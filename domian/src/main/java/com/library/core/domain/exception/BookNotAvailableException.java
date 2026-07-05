package com.library.core.domain.exception;

/**
 * Thrown when a borrow attempt is made on a book that is either
 * already lent out or has been soft-deleted from the library.
 */
public class BookNotAvailableException extends DomainException {
    private static final String DEFAULT_MESSAGE= "The requested book is currently not available for borrowing.";

    public BookNotAvailableException(){
        super(DEFAULT_MESSAGE);
    }

    public BookNotAvailableException(String message){
        super(message);
    }
}
