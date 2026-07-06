package com.library.infrastructure.mysql.repository;

import com.library.infrastructure.mysql.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {

    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbnAndIsDeletedFalse(String isbn);
}
