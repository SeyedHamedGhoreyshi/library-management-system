package com.library.infrastructure.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Password cannot be blank")
    @Size(
        min = 6,
        max = 50,
        message = "Password must be between 6 and 50 characters"
    )
    String password
) {}
