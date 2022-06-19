package com.revo.authservice.infrastructure.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.revo.authservice.domain.port.JwtPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
class JwtAdapter implements JwtPort {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_REPLACEMENT = "";

    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expirationTime}")
    private long expirationTime;

    @Override
    public String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token.replace(TOKEN_PREFIX, TOKEN_REPLACEMENT))
                .getSubject();
    }
}
