package com.library.infrastructure.security.service;

import com.library.core.domain.model.Role;
import com.library.infrastructure.security.access.entity.UserInfo;
import com.library.infrastructure.security.access.repository.UserInfoRepository;
import com.library.infrastructure.security.exception.EmailAlreadyRegisteredException;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final PasswordEncoder encoder;

    public UserInfoService(
        UserInfoRepository repository,
        PasswordEncoder encoder
    ) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = repository.findByEmail(username);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException(
                "User not found with email: " + username
            );
        }

        return new UserInfoDetails(userInfo.get());
    }

    @Transactional
    public UserInfo addUser(UserInfo userInfo) {
        if (repository.existsByEmail(userInfo.getEmail())) {
            throw new EmailAlreadyRegisteredException(userInfo.getEmail());
        }
        if (userInfo.getRole() == null){
            userInfo.setRole(Role.USER);
        }

        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        return repository.save(userInfo);
    }
}
