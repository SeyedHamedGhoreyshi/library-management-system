package com.library.infrastructure.web.security;

import com.library.core.domain.model.Role;
import com.library.infrastructure.security.service.UserInfoDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Role currentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserInfoDetails userDetails) {
            return userDetails.getRole();
        }

        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> {
                    try {
                        return Role.valueOf(authority.substring("ROLE_".length()));
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                })
                .filter(role -> role != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Authenticated user has no recognized role"));
    }
}
