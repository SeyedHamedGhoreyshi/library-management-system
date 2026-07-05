package com.library.core.domain.model;

import com.library.core.domain.exception.BookIsBorrowedException;
import com.library.core.domain.exception.BookNotAvailableException;
import com.library.core.domain.exception.InvalidDomainStateException;
import com.library.core.domain.exception.UnauthorizedDomainActionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {

    private static final String VALID_ISBN_10 = "0306406152";
    private static final String VALID_ISBN_13 = "9780306406157";

    @Nested
    class Register {

        @Test
        void createsUnpersistedAvailableNonDeletedBook() {
            Book book = Book.register("Clean Code", "Robert C. Martin", VALID_ISBN_10, 2008);

            assertThat(book.getId()).isNull();
            assertThat(book.isAvailable()).isTrue();
            assertThat(book.isDeleted()).isFalse();
        }

        @Test
        void stripsWhitespaceFromTitleAndAuthor() {
            Book book = Book.register("  Clean Code  ", "  Robert C. Martin  ", VALID_ISBN_10, 2008);

            assertThat(book.getTitle()).isEqualTo("Clean Code");
            assertThat(book.getAuthor()).isEqualTo("Robert C. Martin");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void throwsWhenTitleIsNullOrBlank(String title) {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register(title, "Author", VALID_ISBN_10, 2008));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void throwsWhenAuthorIsNullOrBlank(String author) {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register("Title", author, VALID_ISBN_10, 2008));
        }

        @Test
        void normalizesHyphenatedIsbn() {
            Book book = Book.register("Title", "Author", "0-306-40615-2", 2008);

            assertThat(book.getIsbn()).isEqualTo("0306406152");
        }

        @Test
        void acceptsValid10DigitIsbn() {
            Book book = Book.register("Title", "Author", "0306406152", 2008);

            assertThat(book.getIsbn()).isEqualTo("0306406152");
        }

        @Test
        void acceptsValid10DigitIsbnEndingInUppercaseX() {
            Book book = Book.register("Title", "Author", "030640615X", 2008);

            assertThat(book.getIsbn()).isEqualTo("030640615X");
        }

        @Test
        void acceptsValid10DigitIsbnEndingInLowercaseX() {
            Book book = Book.register("Title", "Author", "030640615x", 2008);

            assertThat(book.getIsbn()).isEqualTo("030640615x");
        }

        @Test
        void acceptsValid13DigitIsbn() {
            Book book = Book.register("Title", "Author", VALID_ISBN_13, 2008);

            assertThat(book.getIsbn()).isEqualTo(VALID_ISBN_13);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "12345",           // too short
                "12345678901234",  // too long
                "X30640615X",      // letter in wrong position
                "97803064061579"   // 14 digits
        })
        void throwsOnInvalidIsbnFormat(String isbn) {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register("Title", "Author", isbn, 2008));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void throwsWhenIsbnIsNullOrBlank(String isbn) {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register("Title", "Author", isbn, 2008));
        }

        @Test
        void throwsWhenPublicationYearBelowMinimum() {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register("Title", "Author", VALID_ISBN_10, 999));
        }

        @Test
        void throwsWhenPublicationYearAboveMaximum() {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.register("Title", "Author", VALID_ISBN_10, 2101));
        }

        @Test
        void acceptsBoundaryPublicationYears() {
            Book minYearBook = Book.register("Title", "Author", VALID_ISBN_10, 1000);
            Book maxYearBook = Book.register("Title", "Author", VALID_ISBN_13, 2100);

            assertThat(minYearBook.getPublicationYear()).isEqualTo(1000);
            assertThat(maxYearBook.getPublicationYear()).isEqualTo(2100);
        }
    }

    @Nested
    class Reconstitute {

        @Test
        void throwsWhenIdIsNull() {
            assertThrows(InvalidDomainStateException.class,
                    () -> Book.reconstitute(null, "Title", "Author", VALID_ISBN_10, 2008, true, false));
        }

        @Test
        void restoresExactState() {
            Book book = Book.reconstitute(42L, "Title", "Author", VALID_ISBN_10, 2008, false, true);

            assertThat(book.getId()).isEqualTo(42L);
            assertThat(book.getTitle()).isEqualTo("Title");
            assertThat(book.getAuthor()).isEqualTo("Author");
            assertThat(book.getIsbn()).isEqualTo(VALID_ISBN_10);
            assertThat(book.getPublicationYear()).isEqualTo(2008);
            assertThat(book.isAvailable()).isFalse();
            assertThat(book.isDeleted()).isTrue();
        }
    }

    @Nested
    class UpdateDetails {

        @Test
        void updatesFieldsOnNonDeletedBookLeavingIdAndIsbnUnchanged() {
            Book book = Book.reconstitute(1L, "Old Title", "Old Author", VALID_ISBN_10, 2000, true, false);

            book.updateDetails("New Title", "New Author", 2010);

            assertThat(book.getId()).isEqualTo(1L);
            assertThat(book.getIsbn()).isEqualTo(VALID_ISBN_10);
            assertThat(book.getTitle()).isEqualTo("New Title");
            assertThat(book.getAuthor()).isEqualTo("New Author");
            assertThat(book.getPublicationYear()).isEqualTo(2010);
        }

        @Test
        void throwsWhenBookIsDeleted() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, true);

            assertThrows(InvalidDomainStateException.class,
                    () -> book.updateDetails("New Title", "New Author", 2010));
        }

        @Test
        void throwsWhenNewTitleIsBlank() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThrows(InvalidDomainStateException.class,
                    () -> book.updateDetails("   ", "New Author", 2010));
        }

        @Test
        void throwsWhenNewAuthorIsBlank() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThrows(InvalidDomainStateException.class,
                    () -> book.updateDetails("New Title", "   ", 2010));
        }
    }

    @Nested
    class Borrow {

        @Test
        void throwsWhenBookIsUnpersisted() {
            Book book = Book.register("Title", "Author", VALID_ISBN_10, 2000);

            assertThrows(InvalidDomainStateException.class,
                    () -> book.borrow("Alice", LocalDate.of(2024, 1, 1)));
        }

        @Test
        void throwsWhenBookAlreadyBorrowed() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, false);

            assertThrows(BookNotAvailableException.class,
                    () -> book.borrow("Alice", LocalDate.of(2024, 1, 1)));
        }

        @Test
        void throwsWhenBookIsSoftDeleted() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, true);

            assertThrows(BookNotAvailableException.class,
                    () -> book.borrow("Alice", LocalDate.of(2024, 1, 1)));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void throwsWhenBorrowerNameIsNullOrBlank(String borrowerName) {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThrows(InvalidDomainStateException.class,
                    () -> book.borrow(borrowerName, LocalDate.of(2024, 1, 1)));
        }

        @Test
        void throwsWhenBorrowDateIsNull() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThrows(InvalidDomainStateException.class, () -> book.borrow("Alice", null));
        }

        @Test
        void succeedsAndFlipsAvailabilityReturningBorrowRecord() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);
            LocalDate borrowDate = LocalDate.of(2024, 1, 1);

            BorrowRecord record = book.borrow("  Alice  ", borrowDate);

            assertThat(book.isAvailable()).isFalse();
            assertThat(record.getBookId()).isEqualTo(1L);
            assertThat(record.getBorrowerName()).isEqualTo("Alice");
            assertThat(record.getBorrowDate()).isEqualTo(borrowDate);
            assertThat(record.getReturnDate()).isNull();
        }
    }

    @Nested
    class ReturnBook {

        @Test
        void throwsWhenBookIsSoftDeleted() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, true);
            BorrowRecord record = new BorrowRecord(1L, "Alice", LocalDate.of(2024, 1, 1));

            assertThrows(InvalidDomainStateException.class,
                    () -> book.returnBook(record, LocalDate.of(2024, 2, 1)));
        }

        @Test
        void throwsWhenBookIsCurrentlyAvailable() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);
            BorrowRecord record = new BorrowRecord(1L, "Alice", LocalDate.of(2024, 1, 1));

            assertThrows(InvalidDomainStateException.class,
                    () -> book.returnBook(record, LocalDate.of(2024, 2, 1)));
        }

        @Test
        void throwsWhenBorrowRecordBookIdMismatches() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, false);
            BorrowRecord record = new BorrowRecord(2L, "Alice", LocalDate.of(2024, 1, 1));

            assertThrows(InvalidDomainStateException.class,
                    () -> book.returnBook(record, LocalDate.of(2024, 2, 1)));
        }

        @Test
        void throwsWhenReturnDateIsBeforeBorrowDate() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, false);
            BorrowRecord record = new BorrowRecord(1L, "Alice", LocalDate.of(2024, 1, 10));

            assertThrows(InvalidDomainStateException.class,
                    () -> book.returnBook(record, LocalDate.of(2024, 1, 1)));
        }

        @Test
        void succeedsAndFlipsAvailabilityReturningNewClosedRecord() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, false);
            BorrowRecord original = new BorrowRecord(1L, "Alice", LocalDate.of(2024, 1, 1));
            LocalDate returnDate = LocalDate.of(2024, 2, 1);

            BorrowRecord closed = book.returnBook(original, returnDate);

            assertThat(book.isAvailable()).isTrue();
            assertThat(closed).isNotSameAs(original);
            assertThat(closed.getBookId()).isEqualTo(original.getBookId());
            assertThat(closed.getBorrowerName()).isEqualTo(original.getBorrowerName());
            assertThat(closed.getBorrowDate()).isEqualTo(original.getBorrowDate());
            assertThat(closed.getReturnDate()).isEqualTo(returnDate);
            assertThat(original.getReturnDate()).isNull();
        }
    }

    @Nested
    class SoftDelete {

        @Test
        void throwsWhenActorIsNotLibrarian() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThrows(UnauthorizedDomainActionException.class, () -> book.softDelete(Role.USER));
        }

        @Test
        void throwsWhenAlreadyDeleted() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, true);

            assertThrows(InvalidDomainStateException.class, () -> book.softDelete(Role.LIBRARIAN));
        }

        @Test
        void throwsWhenBookIsCurrentlyBorrowed() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, false, false);

            assertThrows(BookIsBorrowedException.class, () -> book.softDelete(Role.LIBRARIAN));
        }

        @Test
        void succeedsWhenLibrarianDeletesAvailableBook() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            book.softDelete(Role.LIBRARIAN);

            assertThat(book.isDeleted()).isTrue();
            assertThat(book.isAvailable()).isFalse();
        }
    }

    @Nested
    class EqualsAndHashCode {

        @Test
        void booksWithSameIdAreEqual() {
            Book book1 = Book.reconstitute(1L, "Title A", "Author A", VALID_ISBN_10, 2000, true, false);
            Book book2 = Book.reconstitute(1L, "Title B", "Author B", VALID_ISBN_13, 2010, false, true);

            assertThat(book1).isEqualTo(book2);
            assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
        }

        @Test
        void unpersistedBooksWithSameIsbnAreEqual() {
            Book book1 = Book.register("Title A", "Author A", VALID_ISBN_10, 2000);
            Book book2 = Book.register("Title B", "Author B", VALID_ISBN_10, 2010);

            assertThat(book1).isEqualTo(book2);
            assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
        }

        @Test
        void booksWithDifferentIdsAreNotEqual() {
            Book book1 = Book.reconstitute(1L, "Title", "Author", VALID_ISBN_10, 2000, true, false);
            Book book2 = Book.reconstitute(2L, "Title", "Author", VALID_ISBN_10, 2000, true, false);

            assertThat(book1).isNotEqualTo(book2);
        }
    }
}
