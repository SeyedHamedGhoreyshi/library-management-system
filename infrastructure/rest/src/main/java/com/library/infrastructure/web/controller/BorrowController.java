package com.library.infrastructure.web.controller;

import com.library.core.application.port.input.command.BorrowBookCommand;
import com.library.core.application.port.input.command.ReturnBookCommand;
import com.library.core.application.port.input.usecase.BorrowBookUseCase;
import com.library.core.application.port.input.usecase.ReturnBookUseCase;
import com.library.infrastructure.web.dto.request.BorrowBookRequest;
import com.library.infrastructure.web.dto.request.ReturnBookRequest;
import com.library.infrastructure.web.dto.response.BorrowRecordResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books/{id}")
@Validated
public class BorrowController {

    private final BorrowBookUseCase borrowBookUseCase;
    private final ReturnBookUseCase returnBookUseCase;

    public BorrowController(BorrowBookUseCase borrowBookUseCase, ReturnBookUseCase returnBookUseCase) {
        this.borrowBookUseCase = borrowBookUseCase;
        this.returnBookUseCase = returnBookUseCase;
    }

    /**
     * POST /api/v1/books/{id}/borrow
     * Authorization: USER or LIBRARIAN.
     * Returns 200 OK with the active BorrowRecord.
     */
    @PostMapping("/borrow")
    public BorrowRecordResponse borrow(
            @PathVariable @Min(value = 1, message = "id must be a positive integer") Long id,
            @Valid @RequestBody BorrowBookRequest request
    ) {
        return BorrowRecordResponse.from(borrowBookUseCase.borrow(new BorrowBookCommand(
                id,
                request.borrowerName(),
                request.borrowDate()
        )));
    }

    /**
     * POST /api/v1/books/{id}/return
     * Authorization: USER or LIBRARIAN.
     * Returns 200 OK with the closed BorrowRecord.
     */
    @PostMapping("/return")
    public BorrowRecordResponse returnBook(
            @PathVariable @Min(value = 1, message = "id must be a positive integer") Long id,
            @Valid @RequestBody ReturnBookRequest request
    ) {
        return BorrowRecordResponse.from(returnBookUseCase.returnBook(new ReturnBookCommand(id, request.returnDate())));
    }
}
