package com.library.infrastructure.security.access.repository;

import com.library.infrastructure.security.access.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);

    boolean existsByEmail(String email);
}
