package com.library.core.application.port.input.usecase;


import com.library.core.application.port.input.command.RegisterBookCommand;
import com.library.core.application.port.input.result.BookResult;

public interface RegisterBookUseCase {
    BookResult register(RegisterBookCommand command);
}
