package com.library.core.application.port.output;

import com.library.core.domain.model.BorrowRecord;

import java.util.Optional;

/**
 * Outbound port for persisting and querying {@link BorrowRecord} value objects.
 * Implemented by the infrastructure persistence adapter.
 */
public interface BorrowRecordRepository {

    /**
     * Persists a new or updated {@link BorrowRecord} and returns the managed instance.
     * Handles both active (open) records created during borrow and closed records
     * produced by {@code BorrowRecord.withReturnDate()} during return.
     */
    BorrowRecord save(BorrowRecord record);

    /**
     * Returns the single open (unreturned) loan record for the given book.
     * Returns {@code Optional.empty()} for books that are currently available —
     * this is the normal state, not an error.
     */
    Optional<BorrowRecord> findActiveByBookId(Long bookId);
}
