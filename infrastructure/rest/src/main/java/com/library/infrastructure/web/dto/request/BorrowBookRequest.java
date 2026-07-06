package com.library.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BorrowBookRequest(

        @NotBlank(message = "borrowerName must not be blank")
        String borrowerName,

        @NotNull(message = "borrowDate must not be null")
        LocalDate borrowDate
) {
}
