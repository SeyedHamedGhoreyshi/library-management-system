package com.library.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record RegisterBookRequest(

        @NotBlank(message = "title must not be blank")
        @NotNull(message = "title must not be null")
        String title,

        @NotBlank(message = "author must not be blank")
        @NotNull(message = "author must not be null")
        String author,

        @NotBlank(message = "isbn must not be blank")
        @NotNull(message = "isbn must not be null")
        @Pattern(
                regexp = "^([0-9Xx-]{10,17})$",
                message = "isbn must be a 10-character or 13-digit ISBN; hyphens are allowed"
        )
        String isbn,

        @NotNull(message = "publication year must not be null")
        @Min(value = 1000, message = "publicationYear must be at least 1000")
        @Max(value = 2100, message = "publicationYear must be at most 2100")
        int publicationYear
) {
}
