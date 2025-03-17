package com.bit.joe.shoppingmall.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        this.secretKey =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Date expirationDate =
                    Jwts.parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getExpiration();
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("token expired");
            return true;
        }
    }

    public String createJwt(String username, String role, Long expiredMs) {
        log.info("createJwt {}", new Date(System.currentTimeMillis()).toString());

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
