package com.library.core.domain.model;

/**
 * Defines the actor roles recognized by the library domain business rules.
 */
public enum Role {
    /** A regular library member with borrowing privileges only. */
    USER,
    /** A librarian with full library management privileges. */
    LIBRARIAN
}
