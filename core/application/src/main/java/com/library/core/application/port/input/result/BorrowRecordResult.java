package com.library.core.application.port.input.result;

import com.library.core.domain.model.BorrowRecord;

import java.time.LocalDate;

public record BorrowRecordResult(
        Long bookId,
        String borrowerName,
        LocalDate borrowDate,
        LocalDate returnDate
) {


    public static BorrowRecordResult from(BorrowRecord record) {
        return new BorrowRecordResult(
                record.getBookId(),
                record.getBorrowerName(),
                record.getBorrowDate(),
                record.getReturnDate()
        );
    }

    public boolean isActive() {
        return returnDate == null;
    }
}
