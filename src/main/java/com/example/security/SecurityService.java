package com.example.security;

import com.example.security.jwt.JwtUtils;
import com.example.spingjwtauthexample.exception.RefreshTokenException;
import com.example.spingjwtauthexample.model.RefreshToken;
import com.example.spingjwtauthexample.model.User;
import com.example.spingjwtauthexample.repository.UserRepository;
import com.example.spingjwtauthexample.service.RefreshTokenService;
import com.example.web.dto.AuthResponse;
import com.example.web.dto.CreateUserRequest;
import com.example.web.dto.LoginRequest;
import com.example.web.dto.RefreshTokenRequest;
import com.example.web.dto.RefreshTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticationUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        RefreshToken refreshToken = refreshTokenService.create(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public void register(CreateUserRequest createUserRequest) {

        var user = User.builder()
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .password(createUserRequest.getPassword())
                .build();

        user.setRoles(createUserRequest.getRoles());
        userRepository.save(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        String requestTokenRefresh = request.getRefreshToken();
        return refreshTokenService.getByRefreshToken(requestTokenRefresh)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getId)
                .map(u -> {
                    User user = userRepository.findById(u).orElseThrow(() ->
                            new RefreshTokenException("Exception for userId: " + u));

                    String token = jwtUtils.generateTokenFromUserName(user.getUsername());
                    return new RefreshTokenResponse(token, refreshTokenService.create(u).getToken());
                }).orElseThrow(() -> new RefreshTokenException("Token is not found!"));
    }

    public void logout() {

        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
        }
    }
}
