package com.library.infrastructure.mysql.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(name = "publication_year", nullable = false)
    private int publicationYear;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Version
    @Column(nullable = false)
    private Long version;

}
