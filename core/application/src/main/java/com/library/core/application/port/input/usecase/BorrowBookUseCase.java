package com.library.core.application.port.input.usecase;


import com.library.core.application.port.input.command.BorrowBookCommand;
import com.library.core.application.port.input.result.BorrowRecordResult;


public interface BorrowBookUseCase {

    BorrowRecordResult borrow(BorrowBookCommand command);
}
