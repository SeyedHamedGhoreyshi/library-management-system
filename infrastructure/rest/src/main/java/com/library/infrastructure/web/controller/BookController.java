package com.library.infrastructure.web.controller;


import com.library.core.application.port.input.command.DeleteBookCommand;
import com.library.core.application.port.input.command.RegisterBookCommand;
import com.library.core.application.port.input.command.UpdateBookCommand;
import com.library.core.application.port.input.query.GetBookDetailsQuery;
import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.application.port.input.usecase.*;
import com.library.infrastructure.web.dto.request.RegisterBookRequest;
import com.library.infrastructure.web.dto.request.UpdateBookRequest;
import com.library.infrastructure.web.dto.response.BookResponse;
import com.library.infrastructure.web.dto.response.PagedBookResponse;
import com.library.infrastructure.web.security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/books")
@Validated
public class BookController {
    private final RegisterBookUseCase registerBookUseCase;
    private final UpdateBookUseCase updateBookUseCase;
    private final DeleteBookUseCase deleteBookUseCase;

    private final GetBooksUseCase getBooksUseCase;
    private final GetBookDetailsUseCase getBookDetailsUseCase;

    public BookController(
            RegisterBookUseCase registerBookUseCase,
            UpdateBookUseCase updateBookUseCase,
            DeleteBookUseCase deleteBookUseCase,
            GetBooksUseCase getBooksUseCase,
            GetBookDetailsUseCase getBookDetailsUseCase
    ){
        this.registerBookUseCase = registerBookUseCase;
        this.updateBookUseCase = updateBookUseCase;
        this.deleteBookUseCase = deleteBookUseCase;
        this.getBooksUseCase = getBooksUseCase;
        this.getBookDetailsUseCase = getBookDetailsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse register(
            @Valid @RequestBody RegisterBookRequest request
    ) {
        return BookResponse.from(registerBookUseCase.register(new RegisterBookCommand(
                request.title(),
                request.author(),
                request.isbn(),
                request.publicationYear()
        )));
    }


    /**
     * PUT /api/v1/books/{id}
     * Authorization: LIBRARIAN only.
     */
    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable @Min(value = 1, message = "id must be a positive integer") Long id,
            @Valid @RequestBody UpdateBookRequest request
    ) {
        return BookResponse.from(updateBookUseCase.update(new UpdateBookCommand(
                id,
                request.title(),
                request.author(),
                request.publicationYear(),
                SecurityUtils.currentUserRole()
        )));
    }

    /**
     * DELETE /api/v1/books/{id}
     * Authorization: LIBRARIAN only.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Min(value = 1, message = "id must be a positive integer") Long id
    ) {
        deleteBookUseCase.delete(new DeleteBookCommand(id, SecurityUtils.currentUserRole()));
    }

    /**
     * GET /api/v1/books/{id}
     * Authorization: USER or LIBRARIAN.
     * Returns 404 Not Found if the book has been soft-deleted.
     */
    @GetMapping("/{id}")
    public BookResponse getBookDetails(
            @PathVariable @Min(value = 1, message = "id must be a positive integer") Long id
    ) {
        BookResult result = getBookDetailsUseCase.getBookDetails(new GetBookDetailsQuery(id));
        return BookResponse.from(result);
    }

    /**
     * GET /api/v1/books
     * Authorization: USER or LIBRARIAN.
     * Supports pagination, sorting, and dynamic filtering.
     */
    @GetMapping
    public PagedBookResponse getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(required = false) Boolean isAvailable
    ) {
        GetBooksQuery query = new GetBooksQuery(
                page, size, sortBy, direction, title, author, publicationYear, isAvailable
        );

        PageResult<BookResult> results = getBooksUseCase.getBooks(query);

        return PagedBookResponse.from(results);
    }

}
