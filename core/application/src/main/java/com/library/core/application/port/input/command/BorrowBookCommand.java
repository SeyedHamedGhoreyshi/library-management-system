package com.library.core.application.port.input.command;

import com.library.core.application.port.exception.InvalidCommandException;
import java.time.LocalDate;

public record BorrowBookCommand(
    Long bookId,
    String borrowerName,
    LocalDate borrowDate
) {
    public BorrowBookCommand {

        if (bookId == null || bookId <= 0) {
            throw new InvalidCommandException(
                "bookId must not be null and must be a positive integer"
            );
        }

        if (borrowerName == null || borrowerName.isBlank()) {
            throw new InvalidCommandException(
                "borrowerName must not be null or blank"
            );
        }
        borrowerName = borrowerName.strip();

        if (borrowDate == null) {
            throw new InvalidCommandException("borrowDate must not be null");
        }
    }
}
