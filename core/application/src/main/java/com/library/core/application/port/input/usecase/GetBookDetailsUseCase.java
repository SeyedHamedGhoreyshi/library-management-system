package com.library.core.application.port.input.usecase;

import com.library.core.application.port.input.query.GetBookDetailsQuery;
import com.library.core.application.port.input.result.BookResult;


public interface GetBookDetailsUseCase {

    BookResult getBookDetails(GetBookDetailsQuery query);
}
