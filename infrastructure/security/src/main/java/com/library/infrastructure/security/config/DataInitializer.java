package com.library.infrastructure.security.config;

import com.library.core.domain.model.Role;
import com.library.infrastructure.security.access.entity.UserInfo;
import com.library.infrastructure.security.access.repository.UserInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initFirstLibrarian(UserInfoRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.findByEmail("admin@library.com").isEmpty()) {
                UserInfo admin = new UserInfo();
                admin.setEmail("admin@library.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.LIBRARIAN);
                repository.save(admin);
            }
        };
    }
}
