package com.library.infrastructure.web.controller;

import com.library.core.domain.model.Role;
import com.library.infrastructure.security.access.entity.UserInfo;
import com.library.infrastructure.security.service.JwtService;
import com.library.infrastructure.security.service.UserInfoDetails;
import com.library.infrastructure.security.service.UserInfoService;
import com.library.infrastructure.web.dto.request.LoginRequest;
import com.library.infrastructure.web.dto.request.SignupRequest;
import com.library.infrastructure.web.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserInfoService userInfoService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
        UserInfoService userInfoService,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(request.email());
        userInfo.setPassword(request.password());

        UserInfo saved = userInfoService.addUser(userInfo);
        String token = jwtService.generateToken(saved.getEmail());

        return new AuthResponse(token, saved.getEmail(), saved.getRole());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());

        return new AuthResponse(token, userDetails.getUsername(), userDetails.getRole());
    }

    @PostMapping("/register-librarian")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerLibrarian(@Valid @RequestBody SignupRequest request) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(request.email());
        userInfo.setPassword(request.password());
        userInfo.setRole(Role.LIBRARIAN);

        UserInfo saved = userInfoService.addUser(userInfo);
        String token = jwtService.generateToken(saved.getEmail());

        return new AuthResponse(token, saved.getEmail(), saved.getRole());
    }


}
