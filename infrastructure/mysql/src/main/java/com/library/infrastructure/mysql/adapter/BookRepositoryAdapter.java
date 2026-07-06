package com.library.infrastructure.mysql.adapter;

import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.application.port.output.BookRepository;
import com.library.core.domain.model.Book;
import com.library.infrastructure.mysql.entity.BookEntity;
import com.library.infrastructure.mysql.mapper.BookMapper;
import com.library.infrastructure.mysql.repository.BookJpaRepository;
import com.library.infrastructure.mysql.repository.BookSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA/MySQL implementation of the {@link BookRepository} output port.
 */
@Component
public class BookRepositoryAdapter implements BookRepository {

    private final BookJpaRepository jpaRepository;

    public BookRepositoryAdapter(BookJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Book save(Book book) {
        // The entity carries the version the domain Book was originally read with
        // (see BookMapper), so JPA's optimistic-locking check compares against the
        // version at read time rather than whatever is currently in the database.
        BookEntity entity = BookMapper.toEntity(book);
        BookEntity saved = jpaRepository.save(entity);
        return BookMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return jpaRepository.findById(id)
                .map(BookMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        return jpaRepository.findByIsbn(isbn)
                .map(BookMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return jpaRepository.existsByIsbnAndIsDeletedFalse(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Book> findAll(GetBooksQuery query) {
        // Build pageable with sorting
        Sort sort = Sort.by(
                query.direction().equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                query.sortBy()
        );
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        // Execute query with dynamic filtering
        Page<BookEntity> page = jpaRepository.findAll(BookSpecification.fromQuery(query), pageable);

        // Map to domain
        List<Book> books = page.getContent().stream()
                .map(BookMapper::toDomain)
                .toList();

        return PageResult.of(
                books,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
