package com.library.core.application.service;

import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.exception.DuplicateIsbnException;
import org.springframework.dao.DataIntegrityViolationException;
import com.library.core.application.port.input.command.DeleteBookCommand;
import com.library.core.application.port.input.command.RegisterBookCommand;
import com.library.core.application.port.input.command.UpdateBookCommand;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.usecase.DeleteBookUseCase;
import com.library.core.application.port.input.usecase.RegisterBookUseCase;
import com.library.core.application.port.input.usecase.UpdateBookUseCase;
import com.library.core.application.port.output.BookRepository;
import com.library.core.domain.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class  BookCommandService implements RegisterBookUseCase, UpdateBookUseCase, DeleteBookUseCase {

    private final BookRepository bookRepository;

    public BookCommandService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookResult register(RegisterBookCommand command) {
        // ISBN uniqueness cannot be enforced by the domain alone — it requires a cross-aggregate query
        if (bookRepository.existsByIsbn(command.isbn())) {
            throw new DuplicateIsbnException(command.isbn());
        }
        Book book = Book.register(command.title(), command.author(), command.isbn(), command.publicationYear());
        try {
            return BookResult.from(bookRepository.save(book));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateIsbnException(command.isbn());
        }
    }

    @Override
    public BookResult update(UpdateBookCommand command) {
        Book book = bookRepository.findById(command.id())
                .orElseThrow(() -> new BookNotFoundException(command.id()));
        book.updateDetails(command.title(), command.author(), command.publicationYear());
        return BookResult.from(bookRepository.save(book));
    }

    @Override
    public BookResult delete(DeleteBookCommand command) {
        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));
        book.softDelete(command.actorRole());
        return BookResult.from(bookRepository.save(book));
    }
}
