package com.library.core.domain.model;

import com.library.core.domain.exception.BookIsBorrowedException;
import com.library.core.domain.exception.BookNotAvailableException;
import com.library.core.domain.exception.InvalidDomainStateException;
import com.library.core.domain.exception.UnauthorizedDomainActionException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

public class Book {

    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{9}[\\dXx]|\\d{13})$");

    private final Long id;
    private String title;
    private String author;
    private final String isbn;
    private int publicationYear;
    private boolean isAvailable;
    private boolean isDeleted;

    private Book(Long id, String title, String author, String isbn, int publicationYear, boolean isAvailable, boolean isDeleted) {
        this.id = id;
        this.title = requireNotBlank(title, "title");
        this.author = requireNotBlank(author, "author");
        this.isbn = requireValidIsbn(isbn);
        this.publicationYear = requireValidYear(publicationYear);
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
    }

    public static Book register(String title, String author, String isbn, int publicationYear) {
        return new Book(null, title, author, isbn, publicationYear, true, false);
    }

    public static Book reconstitute(Long id, String title, String author, String isbn, int publicationYear, boolean isAvailable, boolean isDeleted) {
        return new Book(
                requireNonNull(id , "id"),
                title, author, isbn, publicationYear, isAvailable, isDeleted
        );
    }

    public void updateDetails(String title, String author, int publicationYear) {
        if (isDeleted) {
            throw new InvalidDomainStateException("Cannot update details of a deleted book.");
        }
        this.title = requireNotBlank(title, "title");
        this.author = requireNotBlank(author, "author");
        this.publicationYear = requireValidYear(publicationYear);
    }

    public BorrowRecord borrow(String borrowerName, LocalDate borrowDate) {
        if (this.id == null){
            throw new InvalidDomainStateException("Cannot borrow a book that has not been persisted in the system yet.");
        }
        if (isDeleted) {
            throw new BookNotAvailableException("This book has been deleted.");
        }
        if (!isAvailable) {
            throw new BookNotAvailableException("This book is already borrowed.");
        }

        String validBorrower = requireNotBlank(borrowerName, "borrowerName");
        LocalDate validDate = requireNonNull(borrowDate, "borrowDate must not be null");

        this.isAvailable = false;
        return new BorrowRecord(this.id, validBorrower, validDate);
    }

    public BorrowRecord returnBook(BorrowRecord borrowRecord, LocalDate returnDate) {
        if (isDeleted) {
            throw new InvalidDomainStateException("Cannot perform operations on a deleted book.");
        }
        if (isAvailable) {
            throw new InvalidDomainStateException("Book with ISBN [" + isbn + "] is not currently borrowed.");
        }
        if (this.id != null && !this.id.equals(borrowRecord.getBookId())) {
            throw new InvalidDomainStateException("The provided borrow record does not match this book.");
        }
        this.isAvailable = true;
        return borrowRecord.withReturnDate(returnDate);
    }

    public void softDelete(Role actorRole) {
        if (actorRole != Role.LIBRARIAN) {
            throw new UnauthorizedDomainActionException(
                    "Only a LIBRARIAN may delete books from the catalog. Actor role: [" + actorRole + "]"
            );
        }
        if (isDeleted){
            throw new InvalidDomainStateException("Book is already deleted.");
        }
        if (!isAvailable) {
            throw new BookIsBorrowedException("Cannot delete a book that is currently borrowed.");
        }
        this.isDeleted = true;
        this.isAvailable = false;
    }


    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getPublicationYear() { return publicationYear; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isDeleted() { return isDeleted; }


    private static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainStateException(fieldName + " must not be null or blank");
        }
        return value.strip();
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidDomainStateException(fieldName + " must not be null");
        }
        return value;
    }

    private static String requireValidIsbn(String isbn) {
        requireNotBlank(isbn, "isbn");
        String normalized = isbn.strip().replace("-","");
        if (!ISBN_PATTERN.matcher(normalized).matches()) {
            throw new InvalidDomainStateException("ISBN [" + isbn + "] has an invalid format. Expected 10 or 13 digits.");
        }
        return normalized;
    }

    private static int requireValidYear(int year) {
        if (year < 1000 || year > 2100) {
            throw new InvalidDomainStateException("publicationYear [" + year + "] is not valid");
        }
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null ? id.equals(book.id) : isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', isbn='" + isbn + "', isAvailable=" + isAvailable + "}";
    }
}
