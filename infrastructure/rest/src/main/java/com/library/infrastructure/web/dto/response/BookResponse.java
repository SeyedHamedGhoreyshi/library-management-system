package com.library.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.library.core.application.port.input.result.BookResult;

import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        int publicationYear,
        @JsonProperty("isAvailable") boolean isAvailable
) {
    public static BookResponse from(BookResult result) {
        return new BookResponse(
                result.id(),
                result.title(),
                result.author(),
                result.isbn(),
                result.publicationYear(),
                result.isAvailable()
        );
    }
    public static List<BookResponse> from(List<BookResult> results) {
        return results.stream()
                .map(BookResponse::from)
                .toList();
    }
}
