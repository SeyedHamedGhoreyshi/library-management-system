package com.library.core.application.port.input.command;

import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.domain.model.Role;


public record DeleteBookCommand(Long bookId, Role actorRole) {
    public DeleteBookCommand {
        if (bookId == null || bookId <= 0) {
            throw new InvalidCommandException(
                "bookId must not be null and must be a positive integer"
            );
        }

        if (actorRole == null) {
            throw new InvalidCommandException("actorRole must not be null");
        }
    }
}
