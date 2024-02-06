package com.example.web.controller;

import com.example.security.SecurityService;
import com.example.spingjwtauthexample.exception.AlreadyExistsException;
import com.example.spingjwtauthexample.repository.RefreshTokenRepository;
import com.example.spingjwtauthexample.repository.UserRepository;
import com.example.web.dto.AuthResponse;
import com.example.web.dto.CreateUserRequest;
import com.example.web.dto.LoginRequest;
import com.example.web.dto.RefreshTokenRequest;
import com.example.web.dto.RefreshTokenResponse;
import com.example.web.dto.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    private final SecurityService securityService;

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(securityService.authenticationUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email or Username already taken!");
        }

        securityService.register(request);
        return ResponseEntity.ok(new SimpleResponse("User was created!"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails details) {
        securityService.logout();
        return ResponseEntity.ok(new  SimpleResponse("User was logout! Username is: " + details.getUsername()));
    }

}
