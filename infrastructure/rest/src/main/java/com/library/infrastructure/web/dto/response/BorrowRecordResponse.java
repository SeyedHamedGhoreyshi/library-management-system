package com.library.infrastructure.web.dto.response;

import com.library.core.application.port.input.result.BorrowRecordResult;

import java.time.LocalDate;

public record BorrowRecordResponse(
        Long bookId,
        String borrowerName,
        LocalDate borrowDate,
        LocalDate returnDate
) {
    public static BorrowRecordResponse from(BorrowRecordResult result) {
        return new BorrowRecordResponse(
                result.bookId(),
                result.borrowerName(),
                result.borrowDate(),
                result.returnDate()
        );
    }
}
