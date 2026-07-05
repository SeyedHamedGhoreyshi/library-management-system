package com.library.core.application.port.input.result;

import java.util.Collections;
import java.util.List;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    public PageResult {
        content = (content != null)
                ? Collections.unmodifiableList(content)
                : Collections.emptyList();

        if (pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber must be >= 0, got: " + pageNumber);
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be > 0, got: " + pageSize);
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be >= 0, got: " + totalElements);
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages must be >= 0, got: " + totalPages);
        }
    }


    public static <T> PageResult<T> of(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements
    ) {
        int totalPages = (pageSize > 0)
                ? (int) Math.ceil((double) totalElements / pageSize)
                : 0;
        return new PageResult<>(content, pageNumber, pageSize, totalElements, totalPages);
    }


    /** @return {@code true} if this is the first page (pageNumber == 0) */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /** @return {@code true} if this is the last page or the only page */
    public boolean isLast() {
        return totalPages == 0 || pageNumber >= totalPages - 1;
    }

    /** @return {@code true} if a next page exists */
    public boolean hasNext() {
        return !isLast();
    }

    /** @return {@code true} if a previous page exists */
    public boolean hasPrevious() {
        return !isFirst();
    }

    /** @return {@code true} if the content list is non-empty */
    public boolean hasContent() {
        return !content.isEmpty();
    }
}
