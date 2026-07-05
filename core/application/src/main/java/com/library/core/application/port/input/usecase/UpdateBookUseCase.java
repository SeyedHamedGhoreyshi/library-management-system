package com.library.core.application.port.input.usecase;

import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.application.port.input.command.UpdateBookCommand;
import com.library.core.application.port.input.result.BookResult;


public interface UpdateBookUseCase {

    BookResult update(UpdateBookCommand command);
}
