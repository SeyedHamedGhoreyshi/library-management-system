package com.library.core.application.port.input.command;

import com.library.core.application.port.exception.InvalidCommandException;
import java.time.LocalDate;



public record ReturnBookCommand(Long bookId, LocalDate returnDate) {
    public ReturnBookCommand {
        if (bookId == null || bookId <= 0) {
            throw new InvalidCommandException(
                "bookId must not be null and must be a positive integer"
            );
        }

        if (returnDate == null) {
            throw new InvalidCommandException("returnDate must not be null");
        }
    }
}
