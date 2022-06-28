package com.revo.authservice.domain.port;

public interface JwtPort {
    String createTokenFromUsername(String username);
    String getSubjectFromToken(String token);
}
