package com.library.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.result.PageResult;

import java.util.List;

public record PagedBookResponse(
        List<BookResponse> content,
        int currentPage,
        long totalElements,
        int totalPages,
        @JsonProperty("isLast") boolean isLast
) {
    public static PagedBookResponse from(PageResult<BookResult> page) {
        return new PagedBookResponse(
                BookResponse.from(page.content()),
                page.pageNumber(),
                page.totalElements(),
                page.totalPages(),
                page.isLast()
        );
    }
}
