package com.library.core.application.port.input.usecase;

import com.library.core.application.port.exception.ActiveBorrowRecordNotFoundException;
import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.application.port.input.command.ReturnBookCommand;
import com.library.core.application.port.input.result.BorrowRecordResult;


public interface ReturnBookUseCase {

    BorrowRecordResult returnBook(ReturnBookCommand command);
}
