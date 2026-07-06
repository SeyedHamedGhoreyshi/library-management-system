package com.library.infrastructure.mysql.adapter;

import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.core.application.port.input.result.PageResult;
import com.library.core.domain.model.Book;
import com.library.infrastructure.mysql.entity.BookEntity;
import com.library.infrastructure.mysql.repository.BookJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRepositoryAdapterTest {

    private static final String VALID_ISBN = "9780306406157";

    @Mock
    private BookJpaRepository jpaRepository;

    @InjectMocks
    private BookRepositoryAdapter adapter;

    private BookEntity entityWith(Long id, boolean available, boolean deleted, Long version) {
        return BookEntity.builder()
                .id(id)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn(VALID_ISBN)
                .publicationYear(2008)
                .isAvailable(available)
                .isDeleted(deleted)
                .version(version)
                .build();
    }

    @Nested
    class Save {

        @Test
        void carriesTheOriginallyReadVersionOnTheSavedEntityAndReturnsTheMappedResult() {
            Book book = Book.reconstitute(1L, "Clean Code", "Robert C. Martin", VALID_ISBN, 2008, true, false, 3L);
            BookEntity savedEntity = entityWith(1L, true, false, 4L);
            when(jpaRepository.save(any(BookEntity.class))).thenReturn(savedEntity);

            Book result = adapter.save(book);

            ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
            verify(jpaRepository).save(captor.capture());
            assertThat(captor.getValue().getVersion()).isEqualTo(3L);
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getVersion()).isEqualTo(4L);
        }
    }

    @Nested
    class FindById {

        @Test
        void returnsMappedBookWhenFound() {
            when(jpaRepository.findById(1L)).thenReturn(Optional.of(entityWith(1L, true, false, 1L)));

            Optional<Book> result = adapter.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }

        @Test
        void returnsEmptyWhenNotFound() {
            when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(adapter.findById(99L)).isEmpty();
        }
    }

    @Nested
    class FindByIsbn {

        @Test
        void returnsMappedBookWhenFound() {
            when(jpaRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(entityWith(1L, true, false, 1L)));

            Optional<Book> result = adapter.findByIsbn(VALID_ISBN);

            assertThat(result).isPresent();
            assertThat(result.get().getIsbn()).isEqualTo(VALID_ISBN);
        }

        @Test
        void returnsEmptyWhenNotFound() {
            when(jpaRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.empty());

            assertThat(adapter.findByIsbn(VALID_ISBN)).isEmpty();
        }
    }

    @Nested
    class ExistsByIsbn {

        @Test
        void delegatesToExistsByIsbnAndIsDeletedFalse() {
            when(jpaRepository.existsByIsbnAndIsDeletedFalse(VALID_ISBN)).thenReturn(true);

            assertThat(adapter.existsByIsbn(VALID_ISBN)).isTrue();
        }
    }

    @Nested
    class FindAll {

        @Test
        void mapsThePageOfEntitiesToAPageResultOfBooks() {
            GetBooksQuery query = new GetBooksQuery(0, 10, "title", "ASC", null, null, null, null);
            Page<BookEntity> page = new PageImpl<>(
                    List.of(entityWith(1L, true, false, 1L)),
                    PageRequest.of(0, 10),
                    1
            );
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            PageResult<Book> result = adapter.findAll(query);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).getIsbn()).isEqualTo(VALID_ISBN);
            assertThat(result.pageNumber()).isZero();
            assertThat(result.totalElements()).isEqualTo(1);
        }

        @Test
        void buildsADescendingPageableWhenDirectionIsDesc() {
            GetBooksQuery query = new GetBooksQuery(1, 5, "author", "DESC", null, null, null, null);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 5), 0));

            adapter.findAll(query);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(jpaRepository).findAll(any(Specification.class), captor.capture());
            Pageable pageable = captor.getValue();
            assertThat(pageable.getPageNumber()).isEqualTo(1);
            assertThat(pageable.getPageSize()).isEqualTo(5);
            assertThat(pageable.getSort().getOrderFor("author").getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }
}
