package com.revo.authservice.infrastructure.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.port.Jwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
class JwtImp implements Jwt {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_REPLACEMENT = "";
    private JWTVerifier verifier;

    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expirationTime}")
    private long expirationTime;

    @Override
    public String createTokenFromUsername(String username) {
        return TOKEN_PREFIX + JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public String getSubjectFromToken(String token) {
        try{
            return verifier
                    .verify(token.replace(TOKEN_PREFIX, TOKEN_REPLACEMENT))
                    .getSubject();
        } catch (Exception exception){
            throw new BadLoginException();
        }
    }

    @PostConstruct
    void createJwtVerifier(){
        verifier = JWT.require(Algorithm.HMAC256(secret))
                .build();
    }

}
