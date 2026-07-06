package com.library.infrastructure.mysql.mapper;

import com.library.core.domain.model.Book;
import com.library.infrastructure.mysql.entity.BookEntity;

/**
 * Maps between the pure domain {@link Book} aggregate and the JPA {@link BookEntity}.
 */
public class BookMapper {

    /**
     * Converts a domain Book to a JPA entity.
     * Used before saving to the database.
     */
    public static BookEntity toEntity(Book book) {
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .isAvailable(book.isAvailable())
                .isDeleted(book.isDeleted())
                .version(book.getVersion())
                .build();
    }

    /**
     * Reconstitutes a domain Book from a JPA entity.
     * Used after loading from the database.
     */
    public static Book toDomain(BookEntity entity) {
        return Book.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getIsbn(),
                entity.getPublicationYear(),
                entity.isAvailable(),
                entity.isDeleted(),
                entity.getVersion()
        );
    }
}
