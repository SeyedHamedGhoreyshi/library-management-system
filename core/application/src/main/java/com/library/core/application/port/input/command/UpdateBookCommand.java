package com.library.core.application.port.input.command;

import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.domain.model.Role;


public record UpdateBookCommand(
    Long id,
    String title,
    String author,
    int publicationYear,
    Role actorRole
) {
    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 2100;

    public UpdateBookCommand {

        if (id == null || id <= 0) {
            throw new InvalidCommandException(
                "id must not be null and must be a positive integer"
            );
        }

        if (title == null || title.isBlank()) {
            throw new InvalidCommandException(
                "title must not be null or blank"
            );
        }
        title = title.strip();

        if (author == null || author.isBlank()) {
            throw new InvalidCommandException(
                "author must not be null or blank"
            );
        }
        author = author.strip();

        if (publicationYear < MIN_YEAR || publicationYear > MAX_YEAR) {
            throw new InvalidCommandException(
                "publicationYear [" +
                    publicationYear +
                    "] must be between " +
                    MIN_YEAR +
                    " and " +
                    MAX_YEAR
            );
        }
        if (actorRole == null) {
            throw new InvalidCommandException("actorRole must not be null");
        }
    }
}
