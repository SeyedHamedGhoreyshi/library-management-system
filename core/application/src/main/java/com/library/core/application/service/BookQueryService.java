package com.library.core.application.service;

import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.input.query.GetBookDetailsQuery;
import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.application.port.input.usecase.GetBookDetailsUseCase;
import com.library.core.application.port.input.usecase.GetBooksUseCase;
import com.library.core.application.port.output.BookRepository;
import com.library.core.domain.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookQueryService implements GetBooksUseCase, GetBookDetailsUseCase {

    private final BookRepository bookRepository;

    public BookQueryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public PageResult<BookResult> getBooks(GetBooksQuery query) {
        PageResult<Book> page = bookRepository.findAll(query);
        List<BookResult> results = page.content().stream()
                .map(BookResult::from)
                .toList();
        return new PageResult<>(results, page.pageNumber(), page.pageSize(), page.totalElements(), page.totalPages());
    }

    @Override
    public BookResult getBookDetails(GetBookDetailsQuery query) {
        return bookRepository.findById(query.bookId())
                .filter(book -> !book.isDeleted())
                .map(BookResult::from)
                .orElseThrow(() -> new BookNotFoundException(query.bookId()));
    }
}
