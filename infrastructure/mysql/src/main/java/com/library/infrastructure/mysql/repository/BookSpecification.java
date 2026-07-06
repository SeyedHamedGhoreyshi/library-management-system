package com.library.infrastructure.mysql.repository;

import com.library.core.application.port.input.query.GetBooksQuery;
import com.library.infrastructure.mysql.entity.BookEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds JPA Specification for dynamic book filtering.
 * Always excludes soft-deleted books.
 */
public class BookSpecification {

    public static Specification<BookEntity> fromQuery(GetBooksQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted books
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            // Title filter (case-insensitive contains)
            if (query.title() != null && !query.title().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + query.title().toLowerCase() + "%"
                ));
            }

            // Author filter (case-insensitive contains)
            if (query.author() != null && !query.author().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")),
                        "%" + query.author().toLowerCase() + "%"
                ));
            }

            // Publication year filter (exact match)
            if (query.publicationYear() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("publicationYear"),
                        query.publicationYear()
                ));
            }

            // Availability filter (exact match)
            if (query.isAvailable() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isAvailable"),
                        query.isAvailable()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
