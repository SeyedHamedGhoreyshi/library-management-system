package com.library.infrastructure.mysql.mapper;

import com.library.core.domain.model.BorrowRecord;
import com.library.infrastructure.mysql.entity.BorrowRecordEntity;

/**
 * Maps between the domain {@link BorrowRecord} value object and the JPA {@link BorrowRecordEntity}.
 */
public class BorrowRecordMapper {

    /**
     * Converts a domain BorrowRecord to a JPA entity.
     * Used before saving to the database.
     */
    public static BorrowRecordEntity toEntity(BorrowRecord record) {
        return BorrowRecordEntity.builder()
                .bookId(record.getBookId())
                .borrowerName(record.getBorrowerName())
                .borrowDate(record.getBorrowDate())
                .returnDate(record.getReturnDate())
                .build();
    }

    /**
     * Converts a domain BorrowRecord to a JPA entity, preserving the entity ID.
     * Used when updating an existing record (e.g., setting returnDate).
     */
    public static BorrowRecordEntity toEntity(BorrowRecord record, Long entityId) {
        return BorrowRecordEntity.builder()
                .id(entityId)
                .bookId(record.getBookId())
                .borrowerName(record.getBorrowerName())
                .borrowDate(record.getBorrowDate())
                .returnDate(record.getReturnDate())
                .build();
    }

    /**
     * Reconstitutes a domain BorrowRecord from a JPA entity.
     * Used after loading from the database.
     */
    public static BorrowRecord toDomain(BorrowRecordEntity entity) {
        BorrowRecord record = new BorrowRecord(
                entity.getBookId(),
                entity.getBorrowerName(),
                entity.getBorrowDate()
        );

        // If the record has been returned, create the closed version
        if (entity.getReturnDate() != null) {
            return record.withReturnDate(entity.getReturnDate());
        }

        return record;
    }
}
