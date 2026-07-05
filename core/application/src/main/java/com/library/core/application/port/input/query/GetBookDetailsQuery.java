package com.library.core.application.port.input.query;

import com.library.core.application.port.exception.InvalidCommandException;


public record GetBookDetailsQuery(Long bookId) {

    public GetBookDetailsQuery {
        if (bookId == null || bookId <= 0) {
            throw new InvalidCommandException(
                    "bookId must not be null and must be a positive integer"
            );
        }
    }
}
