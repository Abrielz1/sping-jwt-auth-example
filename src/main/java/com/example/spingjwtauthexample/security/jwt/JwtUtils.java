package com.example.spingjwtauthexample.security.jwt;

import com.example.spingjwtauthexample.security.AppUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String someSecretKey;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public String generateJwtToken(AppUserDetails userDetails) {
        return generateTokenFromUserName(userDetails.getUsername());
    }

    public String generateTokenFromUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.ES512, someSecretKey)
                .compact();

    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(someSecretKey)
                .parseClaimsJwt(token).getBody().getSubject();
    }

    public Boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(someSecretKey).parseClaimsJwt(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Signature is Invalid: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token is Invalid: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token is Expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token is Unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string is Empty: {}", e.getMessage());
        }
            return false;
    }
}
