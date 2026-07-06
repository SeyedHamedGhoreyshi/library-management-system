package com.library.core.application.port.output;

import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.domain.model.Book;

import java.util.Optional;

/**
 * Outbound port for persisting and querying the {@link Book} aggregate.
 * Implemented by the infrastructure persistence adapter.
 */
public interface BookRepository {

    /**
     * Persists a new or updated {@link Book} and returns the managed instance.
     * When the book has no ID ({@code book.getId() == null}), the returned instance
     * carries the database-assigned surrogate ID.
     */
    Book save(Book book);

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    /**
     * Returns {@code true} if any book (including soft-deleted) with the given ISBN exists.
     * Used to enforce the database-level unique ISBN constraint before delegating to the domain.
     */
    boolean existsByIsbn(String isbn);

    /**
     * Returns a filtered, sorted, and paginated slice of the active catalog.
     * Implementations must always exclude soft-deleted books regardless of the
     * filter values carried in the query.
     */
    PageResult<Book> findAll(GetBooksQuery query);
}
