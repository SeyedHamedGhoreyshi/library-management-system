package com.library.infrastructure.mysql.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BorrowRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "borrower_name", nullable = false, length = 255)
    private String borrowerName;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "return_date")
    private LocalDate returnDate;
}
