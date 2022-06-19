package com.revo.authservice.domain.port;

public interface JwtPort {
    String createToken(String username);
    String getSubject(String token);
}
