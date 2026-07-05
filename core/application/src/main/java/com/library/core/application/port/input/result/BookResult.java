package com.library.core.application.port.input.result;

import com.library.core.domain.model.Book;

public record BookResult(
        Long id,
        String title,
        String author,
        String isbn,
        int publicationYear,
        boolean isAvailable,
        boolean isDeleted
) {

    public static BookResult from(Book book) {
        return new BookResult(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.isAvailable(),
                book.isDeleted()
        );
    }
}
