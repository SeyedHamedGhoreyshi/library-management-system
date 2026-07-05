package com.library.core.application.port.input.query;

import com.library.core.application.port.exception.InvalidCommandException;
import java.util.Set;


public record GetBooksQuery(
    int page,
    int size,
    String sortBy,
    String direction,
    String title,
    String author,
    Integer publicationYear,
    Boolean isAvailable
) {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_BY = "title";
    private static final String DEFAULT_DIRECTION = "ASC";
    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 2100;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "title",
        "author",
        "publicationYear"
    );

    public GetBooksQuery {
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        if (
            sortBy == null ||
            sortBy.isBlank() ||
            !ALLOWED_SORT_FIELDS.contains(sortBy)
        ) {
            sortBy = DEFAULT_SORT_BY;
        }

        if (
            direction == null ||
            (!direction.equalsIgnoreCase("ASC") &&
                !direction.equalsIgnoreCase("DESC"))
        ) {
            direction = DEFAULT_DIRECTION;
        } else {
            direction = direction.toUpperCase();
        }

        if (
            publicationYear != null &&
            (publicationYear < MIN_YEAR || publicationYear > MAX_YEAR)
        ) {
            throw new InvalidCommandException(
                "publicationYear filter [" +
                    publicationYear +
                    "] must be between " +
                    MIN_YEAR +
                    " and " +
                    MAX_YEAR
            );
        }
    }
}
