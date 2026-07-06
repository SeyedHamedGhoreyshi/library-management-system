package com.library.core.application.service;

import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.exception.DuplicateIsbnException;
import com.library.core.application.port.input.command.DeleteBookCommand;
import com.library.core.application.port.input.command.RegisterBookCommand;
import com.library.core.application.port.input.command.UpdateBookCommand;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.output.BookRepository;
import com.library.core.domain.exception.InvalidDomainStateException;
import com.library.core.domain.exception.UnauthorizedDomainActionException;
import com.library.core.domain.model.Book;
import com.library.core.domain.model.Role;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookCommandServiceTest {

    private static final String VALID_ISBN = "9780306406157";

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookCommandService service;

    @Nested
    class Register {

        @Test
        void savesANewBookWhenIsbnIsNotTaken() {
            RegisterBookCommand command = new RegisterBookCommand("Clean Code", "Robert C. Martin", VALID_ISBN, 2008);
            when(bookRepository.existsByIsbn(VALID_ISBN)).thenReturn(false);
            when(bookRepository.save(any(Book.class)))
                    .thenReturn(Book.reconstitute(1L, "Clean Code", "Robert C. Martin", VALID_ISBN, 2008, true, false));

            BookResult result = service.register(command);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.isbn()).isEqualTo(VALID_ISBN);
        }

        @Test
        void throwsDuplicateIsbnWhenIsbnAlreadyExists() {
            RegisterBookCommand command = new RegisterBookCommand("Clean Code", "Robert C. Martin", VALID_ISBN, 2008);
            when(bookRepository.existsByIsbn(VALID_ISBN)).thenReturn(true);

            assertThrows(DuplicateIsbnException.class, () -> service.register(command));

            verify(bookRepository, never()).save(any());
        }

        @Test
        void throwsDuplicateIsbnWhenSaveViolatesTheUniqueConstraintConcurrently() {
            RegisterBookCommand command = new RegisterBookCommand("Clean Code", "Robert C. Martin", VALID_ISBN, 2008);
            when(bookRepository.existsByIsbn(VALID_ISBN)).thenReturn(false);
            when(bookRepository.save(any(Book.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThrows(DuplicateIsbnException.class, () -> service.register(command));
        }
    }

    @Nested
    class Update {

        @Test
        void updatesAnExistingBooksDetails() {
            Book existing = Book.reconstitute(1L, "Old Title", "Old Author", VALID_ISBN, 2000, true, false);
            UpdateBookCommand command = new UpdateBookCommand(1L, "New Title", "New Author", 2010, Role.LIBRARIAN);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BookResult result = service.update(command);

            assertThat(result.title()).isEqualTo("New Title");
            assertThat(result.author()).isEqualTo("New Author");
            assertThat(result.publicationYear()).isEqualTo(2010);
        }

        @Test
        void throwsBookNotFoundWhenBookDoesNotExist() {
            UpdateBookCommand command = new UpdateBookCommand(1L, "New Title", "New Author", 2010, Role.LIBRARIAN);
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(BookNotFoundException.class, () -> service.update(command));

            verify(bookRepository, never()).save(any());
        }

        @Test
        void propagatesDomainExceptionAndDoesNotSaveWhenBookIsDeleted() {
            Book deletedBook = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, false, true);
            UpdateBookCommand command = new UpdateBookCommand(1L, "New Title", "New Author", 2010, Role.LIBRARIAN);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(deletedBook));

            assertThrows(InvalidDomainStateException.class, () -> service.update(command));

            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void softDeletesAnAvailableBookWhenActorIsLibrarian() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, true, false);
            DeleteBookCommand command = new DeleteBookCommand(1L, Role.LIBRARIAN);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BookResult result = service.delete(command);

            assertThat(result.isDeleted()).isTrue();
        }

        @Test
        void throwsBookNotFoundWhenBookDoesNotExist() {
            DeleteBookCommand command = new DeleteBookCommand(1L, Role.LIBRARIAN);
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(BookNotFoundException.class, () -> service.delete(command));

            verify(bookRepository, never()).save(any());
        }

        @Test
        void propagatesUnauthorizedWhenActorIsNotLibrarianAndDoesNotSave() {
            Book book = Book.reconstitute(1L, "Title", "Author", VALID_ISBN, 2000, true, false);
            DeleteBookCommand command = new DeleteBookCommand(1L, Role.USER);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

            assertThrows(UnauthorizedDomainActionException.class, () -> service.delete(command));

            verify(bookRepository, never()).save(any());
        }
    }
}
