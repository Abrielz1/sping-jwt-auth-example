package com.example.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String someSecretKey;

    @Value("${app.jwt.tokenExpiration}")
    private String tokenExpiration;
}