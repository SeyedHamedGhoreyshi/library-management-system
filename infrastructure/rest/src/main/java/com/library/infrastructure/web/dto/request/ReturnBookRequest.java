package com.library.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReturnBookRequest(

        @NotNull(message = "returnDate must not be null")
        LocalDate returnDate
) {
}
