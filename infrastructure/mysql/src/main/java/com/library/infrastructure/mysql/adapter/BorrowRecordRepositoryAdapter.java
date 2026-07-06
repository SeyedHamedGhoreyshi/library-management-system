package com.library.infrastructure.mysql.adapter;

import com.library.core.application.port.output.BorrowRecordRepository;
import com.library.core.domain.model.BorrowRecord;
import com.library.infrastructure.mysql.entity.BorrowRecordEntity;
import com.library.infrastructure.mysql.mapper.BorrowRecordMapper;
import com.library.infrastructure.mysql.repository.BorrowRecordJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA/MySQL implementation of the {@link BorrowRecordRepository} output port.
 * Handles persistence of immutable {@link BorrowRecord} value objects.
 */
@Component
public class BorrowRecordRepositoryAdapter implements BorrowRecordRepository {

    private final BorrowRecordJpaRepository jpaRepository;

    public BorrowRecordRepositoryAdapter(BorrowRecordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public BorrowRecord save(BorrowRecord record) {
        // Check if an active record already exists for this book (update scenario)
        Optional<BorrowRecordEntity> existingEntity = jpaRepository.findByBookIdAndReturnDateIsNull(record.getBookId());

        BorrowRecordEntity entity;
        if (existingEntity.isPresent()) {
            // Update the existing record (e.g., setting returnDate)
            entity = BorrowRecordMapper.toEntity(record, existingEntity.get().getId());
        } else {
            // Create a new record
            entity = BorrowRecordMapper.toEntity(record);
        }

        BorrowRecordEntity saved = jpaRepository.save(entity);
        return BorrowRecordMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowRecord> findActiveByBookId(Long bookId) {
        return jpaRepository.findByBookIdAndReturnDateIsNull(bookId)
                .map(BorrowRecordMapper::toDomain);
    }
}
