package com.library.core.domain.model;

import com.library.core.domain.exception.InvalidDomainStateException;

import java.time.LocalDate;
import java.util.Objects;

public class BorrowRecord {

    private final Long bookId;
    private final String borrowerName;
    private final LocalDate borrowDate;
    private LocalDate returnDate;

    public BorrowRecord(Long bookId, String borrowerName, LocalDate borrowDate) {
        this.bookId = requireNonNull(bookId, "bookId");
        this.borrowerName = requireNotBlank(borrowerName, "borrowerName");
        this.borrowDate = Objects.requireNonNull(borrowDate, "borrowDate must not be null");
    }

    public void markAsReturned(LocalDate returnDate) {
        Objects.requireNonNull(returnDate, "returnDate must not be null");

        if (this.returnDate != null) {
            throw new InvalidDomainStateException("BorrowRecord has already been closed with returnDate=" + this.returnDate);
        }
        if (returnDate.isBefore(borrowDate)) {
            throw new InvalidDomainStateException("Return date cannot be before borrow date.");
        }
        this.returnDate = returnDate;
    }

    private static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainStateException(fieldName + " must not be null or blank");
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidDomainStateException(fieldName + " must not be null");
        }
        return value;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "bookId=" + bookId +
                ", borrowerName='" + borrowerName + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
