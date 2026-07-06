package com.library.core.application.service;

import com.library.core.application.port.exception.ActiveBorrowRecordNotFoundException;
import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.input.command.BorrowBookCommand;
import com.library.core.application.port.input.command.ReturnBookCommand;
import com.library.core.application.port.input.result.BorrowRecordResult;
import com.library.core.application.port.output.BookRepository;
import com.library.core.application.port.output.BorrowRecordRepository;
import com.library.core.domain.exception.BookNotAvailableException;
import com.library.core.domain.model.Book;
import com.library.core.domain.model.BorrowRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    private static final String VALID_ISBN = "9780306406157";
    private static final LocalDate BORROW_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate RETURN_DATE = LocalDate.of(2024, 2, 1);

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private BorrowService service;

    @Nested
    class Borrow {

        @Test
        void borrowsTheBookAndSavesBothTheBookAndTheNewRecord() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, true, false);
            BorrowBookCommand command = new BorrowBookCommand(1L, "Alice", BORROW_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BorrowRecordResult result = service.borrow(command);

            assertThat(result.bookId()).isEqualTo(1L);
            assertThat(result.borrowerName()).isEqualTo("Alice");
            assertThat(result.isActive()).isTrue();
            verify(bookRepository).save(book);
        }

        @Test
        void throwsBookNotFoundWhenBookDoesNotExist() {
            BorrowBookCommand command = new BorrowBookCommand(1L, "Alice", BORROW_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(BookNotFoundException.class, () -> service.borrow(command));

            verify(bookRepository, never()).save(any());
            verify(borrowRecordRepository, never()).save(any());
        }

        @Test
        void propagatesDomainExceptionAndSavesNothingWhenBookIsAlreadyBorrowed() {
            Book borrowedBook = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, false, false);
            BorrowBookCommand command = new BorrowBookCommand(1L, "Alice", BORROW_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(borrowedBook));

            assertThrows(BookNotAvailableException.class, () -> service.borrow(command));

            verify(bookRepository, never()).save(any());
            verify(borrowRecordRepository, never()).save(any());
        }
    }

    @Nested
    class ReturnBook {

        @Test
        void returnsTheBookAndSavesBothTheBookAndTheClosedRecord() {
            Book borrowedBook = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, false, false);
            BorrowRecord activeRecord = new BorrowRecord(1L, "Alice", BORROW_DATE);
            ReturnBookCommand command = new ReturnBookCommand(1L, RETURN_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(borrowedBook));
            when(borrowRecordRepository.findActiveByBookId(1L)).thenReturn(Optional.of(activeRecord));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BorrowRecordResult result = service.returnBook(command);

            assertThat(result.isActive()).isFalse();
            assertThat(result.returnDate()).isEqualTo(RETURN_DATE);
        }

        @Test
        void throwsBookNotFoundWhenBookDoesNotExist() {
            ReturnBookCommand command = new ReturnBookCommand(1L, RETURN_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(BookNotFoundException.class, () -> service.returnBook(command));

            verify(borrowRecordRepository, never()).findActiveByBookId(any());
        }

        @Test
        void throwsActiveBorrowRecordNotFoundWhenNoActiveRecordExistsForTheBook() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, false, false);
            ReturnBookCommand command = new ReturnBookCommand(1L, RETURN_DATE);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(borrowRecordRepository.findActiveByBookId(1L)).thenReturn(Optional.empty());

            assertThrows(ActiveBorrowRecordNotFoundException.class, () -> service.returnBook(command));

            verify(bookRepository, never()).save(any());
        }
    }
}
