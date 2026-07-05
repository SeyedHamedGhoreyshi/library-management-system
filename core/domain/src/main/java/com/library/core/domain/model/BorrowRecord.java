package com.library.core.domain.model;

import com.library.core.domain.exception.InvalidDomainStateException;

import java.time.LocalDate;
import java.util.Objects;

public class BorrowRecord {

    private final Long bookId;
    private final String borrowerName;
    private final LocalDate borrowDate;
    private final LocalDate returnDate;

    public BorrowRecord(Long bookId, String borrowerName, LocalDate borrowDate) {
        this.bookId = requireNonNull(bookId, "bookId");
        this.borrowerName = requireNotBlank(borrowerName, "borrowerName");
        this.borrowDate = requireNonNull(borrowDate, "borrowDate");
        this.returnDate = null;
    }

    private BorrowRecord(Long bookId, String borrowerName, LocalDate borrowDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public BorrowRecord withReturnDate(LocalDate returnDate) {
        requireNonNull(returnDate, "returnDate");
        if (this.returnDate != null) {
            throw new InvalidDomainStateException("BorrowRecord is already closed.");
        }
        if (returnDate.isBefore(this.borrowDate)) {
            throw new InvalidDomainStateException("Return date cannot be before borrow date.");
        }
        return new BorrowRecord(this.bookId, this.borrowerName, this.borrowDate, returnDate);
    }

    private static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainStateException(fieldName + " must not be null or blank");
        }
        return value.strip();
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return Objects.equals(bookId, that.bookId) &&
                Objects.equals(borrowerName, that.borrowerName) &&
                Objects.equals(borrowDate, that.borrowDate) &&
                Objects.equals(returnDate, that.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, borrowerName, borrowDate, returnDate);
    }
}
