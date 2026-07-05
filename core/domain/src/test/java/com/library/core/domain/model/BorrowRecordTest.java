package com.library.core.domain.model;

import com.library.core.domain.exception.InvalidDomainStateException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BorrowRecordTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2024, 1, 1);

    @Nested
    class Construction {

        @Test
        void throwsWhenBookIdIsNull() {
            assertThrows(InvalidDomainStateException.class,
                    () -> new BorrowRecord(null, "Alice", BORROW_DATE));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void throwsWhenBorrowerNameIsNullOrBlank(String borrowerName) {
            assertThrows(InvalidDomainStateException.class,
                    () -> new BorrowRecord(1L, borrowerName, BORROW_DATE));
        }

        @Test
        void stripsWhitespaceFromBorrowerName() {
            BorrowRecord record = new BorrowRecord(1L, "  Alice  ", BORROW_DATE);

            assertThat(record.getBorrowerName()).isEqualTo("Alice");
        }

        @Test
        void throwsWhenBorrowDateIsNull() {
            assertThrows(InvalidDomainStateException.class,
                    () -> new BorrowRecord(1L, "Alice", null));
        }

        @Test
        void newRecordIsOpenWithNullReturnDate() {
            BorrowRecord record = new BorrowRecord(1L, "Alice", BORROW_DATE);

            assertThat(record.getReturnDate()).isNull();
            assertThat(record.isReturned()).isFalse();
        }
    }

    @Nested
    class WithReturnDate {

        @Test
        void throwsWhenReturnDateIsNull() {
            BorrowRecord record = new BorrowRecord(1L, "Alice", BORROW_DATE);

            assertThrows(InvalidDomainStateException.class, () -> record.withReturnDate(null));
        }

        @Test
        void throwsWhenReturnDateIsBeforeBorrowDate() {
            BorrowRecord record = new BorrowRecord(1L, "Alice", BORROW_DATE);

            assertThrows(InvalidDomainStateException.class,
                    () -> record.withReturnDate(BORROW_DATE.minusDays(1)));
        }

        @Test
        void acceptsReturnDateEqualToBorrowDate() {
            BorrowRecord record = new BorrowRecord(1L, "Alice", BORROW_DATE);

            BorrowRecord closed = record.withReturnDate(BORROW_DATE);

            assertThat(closed.getReturnDate()).isEqualTo(BORROW_DATE);
        }

        @Test
        void returnsNewInstanceLeavingOriginalUnmodified() {
            BorrowRecord original = new BorrowRecord(1L, "Alice", BORROW_DATE);
            LocalDate returnDate = BORROW_DATE.plusDays(30);

            BorrowRecord closed = original.withReturnDate(returnDate);

            assertThat(closed).isNotSameAs(original);
            assertThat(closed.getReturnDate()).isEqualTo(returnDate);
            assertThat(closed.isReturned()).isTrue();
            assertThat(original.getReturnDate()).isNull();
            assertThat(original.isReturned()).isFalse();
        }

        @Test
        void throwsWhenRecordIsAlreadyClosed() {
            BorrowRecord original = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord closed = original.withReturnDate(BORROW_DATE.plusDays(10));

            assertThrows(InvalidDomainStateException.class,
                    () -> closed.withReturnDate(BORROW_DATE.plusDays(20)));
        }
    }

    @Nested
    class EqualsAndHashCode {

        @Test
        void openRecordsWithSameFieldsAreEqual() {
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord record2 = new BorrowRecord(1L, "Alice", BORROW_DATE);

            assertThat(record1).isEqualTo(record2);
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }

        @Test
        void closedRecordsWithSameFieldsAreEqual() {
            LocalDate returnDate = BORROW_DATE.plusDays(5);
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE).withReturnDate(returnDate);
            BorrowRecord record2 = new BorrowRecord(1L, "Alice", BORROW_DATE).withReturnDate(returnDate);

            assertThat(record1).isEqualTo(record2);
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }

        @Test
        void recordsDifferingInBookIdAreNotEqual() {
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord record2 = new BorrowRecord(2L, "Alice", BORROW_DATE);

            assertThat(record1).isNotEqualTo(record2);
        }

        @Test
        void recordsDifferingInBorrowerNameAreNotEqual() {
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord record2 = new BorrowRecord(1L, "Bob", BORROW_DATE);

            assertThat(record1).isNotEqualTo(record2);
        }

        @Test
        void recordsDifferingInBorrowDateAreNotEqual() {
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord record2 = new BorrowRecord(1L, "Alice", BORROW_DATE.plusDays(1));

            assertThat(record1).isNotEqualTo(record2);
        }

        @Test
        void recordsDifferingInReturnDateAreNotEqual() {
            BorrowRecord record1 = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord record2 = record1.withReturnDate(BORROW_DATE.plusDays(1));

            assertThat(record1).isNotEqualTo(record2);
        }
    }
}
