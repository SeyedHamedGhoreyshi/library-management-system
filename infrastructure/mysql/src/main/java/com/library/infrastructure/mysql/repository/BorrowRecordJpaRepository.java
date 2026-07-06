package com.library.infrastructure.mysql.repository;

import com.library.infrastructure.mysql.entity.BorrowRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordJpaRepository extends JpaRepository<BorrowRecordEntity, Long> {

    /**
     * Finds the active (unreturned) borrow record for a given book.
     * An active record has a null returnDate.
     */
    Optional<BorrowRecordEntity> findByBookIdAndReturnDateIsNull(Long bookId);
}
