package com.library.infrastructure.mysql.adapter;

import com.library.core.domain.model.BorrowRecord;
import com.library.infrastructure.mysql.entity.BorrowRecordEntity;
import com.library.infrastructure.mysql.repository.BorrowRecordJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowRecordRepositoryAdapterTest {

    private static final LocalDate BORROW_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate RETURN_DATE = LocalDate.of(2024, 2, 1);

    @Mock
    private BorrowRecordJpaRepository jpaRepository;

    @InjectMocks
    private BorrowRecordRepositoryAdapter adapter;

    @Nested
    class Save {

        @Test
        void createsANewEntityWhenNoActiveRecordExistsForTheBook() {
            when(jpaRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.empty());
            BorrowRecordEntity saved = BorrowRecordEntity.builder()
                    .id(10L).bookId(1L).borrowerName("Alice").borrowDate(BORROW_DATE)
                    .build();
            when(jpaRepository.save(any(BorrowRecordEntity.class))).thenReturn(saved);

            BorrowRecord record = new BorrowRecord(1L, "Alice", BORROW_DATE);
            BorrowRecord result = adapter.save(record);

            ArgumentCaptor<BorrowRecordEntity> captor = ArgumentCaptor.forClass(BorrowRecordEntity.class);
            verify(jpaRepository).save(captor.capture());
            assertThat(captor.getValue().getId()).isNull();
            assertThat(result.getBookId()).isEqualTo(1L);
            assertThat(result.isReturned()).isFalse();
        }

        @Test
        void updatesTheExistingActiveRecordWhenOnePresentForTheBook() {
            BorrowRecordEntity existing = BorrowRecordEntity.builder()
                    .id(10L).bookId(1L).borrowerName("Alice").borrowDate(BORROW_DATE)
                    .build();
            when(jpaRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.of(existing));
            BorrowRecordEntity saved = BorrowRecordEntity.builder()
                    .id(10L).bookId(1L).borrowerName("Alice").borrowDate(BORROW_DATE).returnDate(RETURN_DATE)
                    .build();
            when(jpaRepository.save(any(BorrowRecordEntity.class))).thenReturn(saved);

            BorrowRecord closedRecord = new BorrowRecord(1L, "Alice", BORROW_DATE).withReturnDate(RETURN_DATE);
            BorrowRecord result = adapter.save(closedRecord);

            ArgumentCaptor<BorrowRecordEntity> captor = ArgumentCaptor.forClass(BorrowRecordEntity.class);
            verify(jpaRepository).save(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(10L);
            assertThat(result.isReturned()).isTrue();
            assertThat(result.getReturnDate()).isEqualTo(RETURN_DATE);
        }
    }

    @Nested
    class FindActiveByBookId {

        @Test
        void returnsMappedRecordWhenAnActiveRecordExists() {
            BorrowRecordEntity entity = BorrowRecordEntity.builder()
                    .id(10L).bookId(1L).borrowerName("Alice").borrowDate(BORROW_DATE)
                    .build();
            when(jpaRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.of(entity));

            Optional<BorrowRecord> result = adapter.findActiveByBookId(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getBorrowerName()).isEqualTo("Alice");
            assertThat(result.get().isReturned()).isFalse();
        }

        @Test
        void returnsEmptyWhenNoActiveRecordExists() {
            when(jpaRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.empty());

            assertThat(adapter.findActiveByBookId(1L)).isEmpty();
        }
    }
}
