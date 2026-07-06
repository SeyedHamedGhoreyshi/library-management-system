package com.library.infrastructure.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBookRequest(

        @NotBlank(message = "title must not be blank")
        String title,

        @NotBlank(message = "author must not be blank")
        String author,

        @NotNull(message = "publicationYear must not be null")
        @Min(value = 1000, message = "publicationYear must be at least 1000")
        @Max(value = 2026, message = "publicationYear cannot be in future")
        Integer publicationYear
) {
}
