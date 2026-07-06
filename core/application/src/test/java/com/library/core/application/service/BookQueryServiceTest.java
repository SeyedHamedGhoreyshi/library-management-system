package com.library.core.application.service;

import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.input.query.GetBookDetailsQuery;
import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.BookResult;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.application.port.output.BookRepository;
import com.library.core.domain.model.Book;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceTest {

    private static final String VALID_ISBN = "9780306406157";

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookQueryService service;

    @Nested
    class GetBooks {

        @Test
        void mapsThePageOfDomainBooksToAPageOfBookResultsPreservingPagination() {
            GetBooksQuery query = new GetBooksQuery(1, 10, "title", "ASC", null, null, null, null);
            Book book = Book.reconstitute(1L, "Clean Code", "Robert C. Martin", VALID_ISBN, 2008, true, false);
            PageResult<Book> domainPage = PageResult.of(List.of(book), 1, 10, 21);
            when(bookRepository.findAll(query)).thenReturn(domainPage);

            PageResult<BookResult> result = service.getBooks(query);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).isbn()).isEqualTo(VALID_ISBN);
            assertThat(result.pageNumber()).isEqualTo(1);
            assertThat(result.pageSize()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(21);
            assertThat(result.totalPages()).isEqualTo(3);
        }
    }

    @Nested
    class GetBookDetails {

        @Test
        void returnsTheMappedBookWhenFoundAndNotDeleted() {
            Book book = Book.reconstitute(1L, "Clean Code", "Robert C. Martin", VALID_ISBN, 2008, true, false);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

            BookResult result = service.getBookDetails(new GetBookDetailsQuery(1L));

            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        void throwsBookNotFoundWhenNoBookExistsWithTheId() {
            when(bookRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(BookNotFoundException.class, () -> service.getBookDetails(new GetBookDetailsQuery(99L)));
        }

        @Test
        void throwsBookNotFoundWhenTheBookIsSoftDeleted() {
            Book deletedBook = Book.reconstitute(1L, "Clean Code", "Robert C. Martin", VALID_ISBN, 2008, false, true);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(deletedBook));

            assertThrows(BookNotFoundException.class, () -> service.getBookDetails(new GetBookDetailsQuery(1L)));
        }
    }
}
