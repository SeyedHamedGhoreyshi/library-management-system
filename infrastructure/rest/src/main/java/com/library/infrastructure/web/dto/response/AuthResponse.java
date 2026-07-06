package com.library.infrastructure.web.dto.response;

import com.library.core.domain.model.Role;

public record AuthResponse(
        String token,
        String email,
        Role role,
        String tokenType
) {
    public AuthResponse(String token, String email, Role role) {
        this(token, email, role, "Bearer");
    }
}
