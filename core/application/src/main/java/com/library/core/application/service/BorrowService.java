package com.library.core.application.service;

import com.library.core.application.port.exception.ActiveBorrowRecordNotFoundException;
import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.input.command.BorrowBookCommand;
import com.library.core.application.port.input.command.ReturnBookCommand;
import com.library.core.application.port.input.result.BorrowRecordResult;
import com.library.core.application.port.input.usecase.BorrowBookUseCase;
import com.library.core.application.port.input.usecase.ReturnBookUseCase;
import com.library.core.application.port.output.BookRepository;
import com.library.core.application.port.output.BorrowRecordRepository;
import com.library.core.domain.model.Book;
import com.library.core.domain.model.BorrowRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BorrowService implements BorrowBookUseCase, ReturnBookUseCase {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowService(BookRepository bookRepository, BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Override
    public BorrowRecordResult borrow(BorrowBookCommand command) {
        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));
        BorrowRecord record = book.borrow(command.borrowerName(), command.borrowDate());
        bookRepository.save(book);
        return BorrowRecordResult.from(borrowRecordRepository.save(record));
    }

    @Override
    public BorrowRecordResult returnBook(ReturnBookCommand command) {
        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));
        BorrowRecord activeRecord = borrowRecordRepository.findActiveByBookId(command.bookId())
                .orElseThrow(() -> new ActiveBorrowRecordNotFoundException(command.bookId()));
        BorrowRecord closedRecord = book.returnBook(activeRecord, command.returnDate());
        bookRepository.save(book);
        return BorrowRecordResult.from(borrowRecordRepository.save(closedRecord));
    }
}
