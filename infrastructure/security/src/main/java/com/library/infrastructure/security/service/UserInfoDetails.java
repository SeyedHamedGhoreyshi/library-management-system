package com.library.infrastructure.security.service;

import com.library.core.domain.model.Role;
import com.library.infrastructure.security.access.entity.UserInfo;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInfoDetails implements UserDetails {

    private final String username; // email
    private final String password;
    private final Role role;
    private final List<GrantedAuthority> authorities;

    public UserInfoDetails(UserInfo userInfo) {
        this.username = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.role = userInfo.getRole();
        this.authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + userInfo.getRole().name())
        );
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
