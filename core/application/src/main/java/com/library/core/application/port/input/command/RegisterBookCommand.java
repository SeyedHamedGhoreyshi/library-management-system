package com.library.core.application.port.input.command;

import com.library.core.application.port.exception.InvalidCommandException;
import java.util.regex.Pattern;

public record RegisterBookCommand(
    String title,
    String author,
    String isbn,
    int publicationYear
) {
    /** Matches a plain 10-digit ISBN (last char may be X) or a plain 13-digit ISBN. */
    private static final Pattern ISBN_PATTERN = Pattern.compile(
        "^(\\d{9}[\\dXx]|\\d{13})$"
    );

    private static final int MIN_YEAR = 1000;
    private static final int MAX_YEAR = 2100;

    public RegisterBookCommand {
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

        if (isbn == null || isbn.isBlank()) {
            throw new InvalidCommandException("isbn must not be null or blank");
        }
        String normalizedIsbn = isbn.strip().replace("-", "");
        if (!ISBN_PATTERN.matcher(normalizedIsbn).matches()) {
            throw new InvalidCommandException(
                "isbn [" +
                    isbn +
                    "] is invalid. Expected a 10-digit or 13-digit ISBN (hyphens are allowed)."
            );
        }
        isbn = normalizedIsbn;

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
    }
}
