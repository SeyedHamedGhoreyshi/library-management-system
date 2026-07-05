package com.library.core.application.port.input.usecase;

import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.result.PageResult;


public interface GetBooksUseCase {

    PageResult<BookResult> getBooks(GetBooksQuery query);
}
